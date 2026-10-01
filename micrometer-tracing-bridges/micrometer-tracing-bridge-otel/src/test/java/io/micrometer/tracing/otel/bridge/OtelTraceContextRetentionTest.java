/**
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class OtelTraceContextRetentionTest {

    private static final int ITERATIONS = 1_000;

    private OtelTracer tracer() {
        SdkTracerProvider provider = SdkTracerProvider.builder().build();
        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder().setTracerProvider(provider).build();
        return new OtelTracer(sdk.getTracer("repro"), new OtelCurrentTraceContext(), event -> {
        });
    }

    @Test
    void balancedScopesDoNotRetain() throws Exception {
        assertThat(onFreshThread(this::balancedLoop)).isZero();
    }

    @Test
    void outOfOrderScopeCloseDoesNotRetainUnboundedChain() throws Exception {
        assertThat(onFreshThread(this::outOfOrderLoop)).isZero();
    }

    @Test
    void singleOutOfOrderPairThenBalancedLoopDoesNotRetainChain() throws Exception {
        assertThat(onFreshThread(() -> {
            OtelTracer tracer = tracer();
            Span span0 = tracer.spanBuilder().setNoParent().name("s0").start();
            Tracer.SpanInScope scope0 = tracer.withSpan(span0);

            Span span1 = tracer.spanBuilder().setNoParent().name("s1").start();
            Tracer.SpanInScope scope1 = tracer.withSpan(span1);

            scope0.close();
            span0.end();

            scope1.close();
            span1.end();

            Span last = null;
            for (int i = 2; i < ITERATIONS; i++) {
                Span span = tracer.spanBuilder().setNoParent().name("s" + i).start();
                try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
                    // no work
                }
                finally {
                    span.end();
                }
                last = span;
            }
            return retainedChainDepth(Objects.requireNonNull(last).context());
        })).isZero();
    }

    @Test
    void asyncHandoffCloseOnDifferentThreadDoesNotRetainChain() throws Exception {
        ExecutorService workerPool = Executors.newSingleThreadExecutor();
        ExecutorService asyncPool = Executors.newSingleThreadExecutor();
        try {
            OtelTracer tracer = tracer();
            TraceContext[] lastContext = new TraceContext[1];

            for (int i = 0; i < 100; i++) {
                final int idx = i;
                Future<Tracer.SpanInScope> future = workerPool.submit(() -> {
                    Span span = tracer.spanBuilder().setNoParent().name("s" + idx).start();
                    Tracer.SpanInScope scope = tracer.withSpan(span);
                    span.end();
                    lastContext[0] = span.context();
                    return scope;
                });
                Tracer.SpanInScope scope = future.get();
                asyncPool.submit(scope::close).get();
            }

            assertThat(retainedChainDepth(Objects.requireNonNull(lastContext[0]))).isZero();
        }
        finally {
            workerPool.shutdown();
            asyncPool.shutdown();
        }
    }

    private int balancedLoop() {
        OtelTracer tracer = tracer();
        Span last = null;
        for (int i = 0; i < ITERATIONS; i++) {
            Span span = tracer.spanBuilder().setNoParent().name("s" + i).start();
            try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
                // no work
            }
            finally {
                span.end();
            }
            last = span;
        }
        return retainedChainDepth(Objects.requireNonNull(last).context());
    }

    private int outOfOrderLoop() {
        OtelTracer tracer = tracer();
        Span last = null;
        Tracer.SpanInScope previous = null;

        for (int i = 0; i < ITERATIONS; i++) {
            Span span = tracer.spanBuilder().setNoParent().name("s" + i).start();
            Tracer.SpanInScope scope = tracer.withSpan(span);
            if (previous != null) {
                previous.close();
            }
            previous = scope;
            span.end();
            last = span;
        }
        if (previous != null) {
            previous.close();
        }
        return retainedChainDepth(Objects.requireNonNull(last).context());
    }

    private static int onFreshThread(java.util.function.IntSupplier body) throws Exception {
        int[] result = new int[1];
        Thread thread = new Thread(() -> result[0] = body.getAsInt(), "repro");
        thread.start();
        thread.join();
        return result[0];
    }

    private static int retainedChainDepth(TraceContext traceContext) {
        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        int depth = 0;
        Object current = traceContext;
        while (current != null && seen.add(current)) {
            Context context = capturedContext(current);
            if (context == null) {
                break;
            }
            Object next = traceContextIn(context, seen);
            if (next == null) {
                break;
            }
            current = next;
            depth++;
        }
        return depth;
    }

    @SuppressWarnings("unchecked")
    private static @Nullable Context capturedContext(Object traceContext) {
        try {
            Field field = traceContext.getClass().getDeclaredField("otelContext");
            field.setAccessible(true);
            return ((AtomicReference<Context>) field.get(traceContext)).get();
        }
        catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static @Nullable Object traceContextIn(Context context, Set<Object> seen) {
        try {
            Field field = context.getClass().getDeclaredField("entries");
            field.setAccessible(true);
            for (Object entry : (Object[]) field.get(context)) {
                if (entry instanceof OtelTraceContext && !seen.contains(entry)) {
                    return entry;
                }
            }
        }
        catch (ReflectiveOperationException ignored) {
            // fall through
        }
        return null;
    }

}
