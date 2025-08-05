/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * A test implementation of a current trace context.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class SimpleCurrentTraceContext implements CurrentTraceContext {

    private final SimpleTracer simpleTracer;

    /**
     * Creates a new instance of {@link SimpleCurrentTraceContext}.
     * @param simpleTracer simple tracer
     */
    public SimpleCurrentTraceContext(SimpleTracer simpleTracer) {
        this.simpleTracer = simpleTracer;
    }

    @Override
    public TraceContext context() {
        Span span = this.simpleTracer.currentSpan();
        if (span != null) {
            return span.context();
        }
        return null;
    }

    @Override
    public Scope newScope(TraceContext context) {
        if (context == null) {
            SimpleTracer.resetCurrentSpan();
            return Scope.NOOP;
        }
        SimpleSpan previous = SimpleTracer.getCurrentSpan();
        SimpleTracer.setCurrentSpan(context);
        Map<String, String> baggageFromParent = ((SimpleTraceContext) context).baggageFromParent();
        List<BaggageInScope> baggageInScope = baggageFromParent.entrySet()
            .stream()
            .map(entry -> simpleTracer.simpleBaggageManager.createBaggageInScope(context, entry.getKey(),
                    entry.getValue()))
            .collect(Collectors.toList());
        return previous != null ? new RevertToPreviousScope(previous, baggageInScope) : new RevertToNullScope();
    }

    @Override
    public Scope maybeScope(TraceContext context) {
        if (context == null) {
            SimpleTracer.resetCurrentSpan();
            return Scope.NOOP;
        }
        SimpleSpan current = SimpleTracer.getCurrentSpan();
        if (Objects.equals(current != null ? current.context() : current, context)) {
            return Scope.NOOP;
        }
        return newScope(context);
    }

    @Override
    public <C> Callable<C> wrap(Callable<C> task) {
        return task;
    }

    @Override
    public Runnable wrap(Runnable task) {
        return task;
    }

    @Override
    public Executor wrap(Executor delegate) {
        return delegate;
    }

    @Override
    public ExecutorService wrap(ExecutorService delegate) {
        return delegate;
    }

    private static final class RevertToNullScope implements Scope {

        @Override
        public void close() {
            SimpleTracer.resetCurrentSpan();
        }

    }

    private static final class RevertToPreviousScope implements Scope {

        final SimpleSpan previous;

        final List<BaggageInScope> baggageInScope;

        RevertToPreviousScope(SimpleSpan previous, List<BaggageInScope> baggageInScope) {
            this.previous = previous;
            this.baggageInScope = baggageInScope;
        }

        @Override
        public void close() {
            SimpleTracer.setCurrentSpan(this.previous);
            this.baggageInScope.forEach(BaggageInScope::close);
        }

    }

}
