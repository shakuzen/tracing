/*
 * Copyright © 2017 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2017 VMware, Inc.
 */
package io.micrometer.benchmark.tracer;

import brave.Span;
import brave.Tracing;
import brave.handler.SpanHandler;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.propagation.TraceContextOrSamplingFlags;
import brave.sampler.Sampler;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.Throughput)
public class BraveTracerBenchmark implements MicrometerTracingBenchmarks {

    @State(Scope.Benchmark)
    public static class MicrometerTracingState {

        @Param({ "5" })
        public int childSpanCount;

        ThreadLocalCurrentTraceContext braveCurrentTraceContext;

        BraveCurrentTraceContext bridgeContext;

        Tracing tracing;

        brave.Tracer braveTracer;

        Tracer tracer;

        @Setup
        public void setup() {
            this.braveCurrentTraceContext = ThreadLocalCurrentTraceContext.newBuilder().build();
            this.bridgeContext = new BraveCurrentTraceContext(this.braveCurrentTraceContext);
            this.tracing = Tracing.newBuilder()
                .currentTraceContext(this.braveCurrentTraceContext)
                .sampler(Sampler.NEVER_SAMPLE)
                .addSpanHandler(SpanHandler.NOOP)
                .build();
            this.tracing.setNoop(true);
            this.braveTracer = this.tracing.tracer();
            this.tracer = new BraveTracer(this.braveTracer, this.bridgeContext, new BraveBaggageManager());
        }

        @TearDown
        public void close() {
            this.tracing.close();
        }

    }

    @Benchmark
    public void micrometerTracing(MicrometerTracingState state, Blackhole blackhole) {
        micrometerTracing(state.tracer, state.childSpanCount, blackhole);
    }

    @Benchmark
    public io.micrometer.tracing.Span micrometerTracingNewSpan(MicrometerTracingState state) {
        return micrometerTracingNewSpan(state.tracer);
    }

    @Benchmark
    public void micrometerTracingWithScope(MicrometerTracingState state, Blackhole blackhole) {
        micrometerTracingWithScope(state.tracer, state.childSpanCount, blackhole);
    }

    @State(Scope.Benchmark)
    public static class BraveState {

        @Param({ "5" })
        public int childSpanCount;

        ThreadLocalCurrentTraceContext braveCurrentTraceContext;

        Tracing tracing;

        brave.Tracer tracer;

        @Setup
        public void setup() {
            this.braveCurrentTraceContext = ThreadLocalCurrentTraceContext.newBuilder().build();
            this.tracing = Tracing.newBuilder()
                .currentTraceContext(this.braveCurrentTraceContext)
                .sampler(Sampler.NEVER_SAMPLE)
                .addSpanHandler(SpanHandler.NOOP)
                .build();
            this.tracing.setNoop(true);
            this.tracer = this.tracing.tracer();
        }

        @TearDown
        public void close() {
            this.tracing.close();
        }

    }

    @Benchmark
    public void braveTracing(BraveState state, Blackhole blackhole) {
        brave.Span parentSpan = state.tracer.nextSpan().name("parent-span").start();
        TraceContextOrSamplingFlags traceContext = TraceContextOrSamplingFlags.create(parentSpan.context());
        for (int i = 0; i < state.childSpanCount; i++) {
            brave.Span span = state.tracer.nextSpan(traceContext).name("new-span" + i);
            span.start().tag("key", "value").annotate("event").finish();
            blackhole.consume(span);
        }
        parentSpan.finish();
        blackhole.consume(parentSpan);
    }

    @Benchmark
    public Span braveTracingNewSpan(BraveState state, Blackhole blackhole) {
        brave.Span span = state.tracer.nextSpan().name("child-span").start();
        span.finish();
        return span;
    }

    @Benchmark
    public void braveTracingWithScope(BraveState state, Blackhole blackhole) {
        brave.Span parentSpan = state.tracer.nextSpan().name("parent-span").start();
        try (brave.Tracer.SpanInScope spanInScope = state.tracer.withSpanInScope(parentSpan)) {
            for (int i = 0; i < state.childSpanCount; i++) {
                brave.Span childSpan = state.tracer.nextSpan().name("new-span" + i);
                try (brave.Tracer.SpanInScope spanInScope2 = state.tracer.withSpanInScope(childSpan.start())) {
                    childSpan.tag("key", "value").annotate("event");
                }
                childSpan.finish();
                blackhole.consume(childSpan);
            }
        }
        parentSpan.finish();
        blackhole.consume(parentSpan);
    }

}
