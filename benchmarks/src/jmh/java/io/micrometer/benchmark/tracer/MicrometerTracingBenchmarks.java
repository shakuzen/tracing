/*
 * Copyright © 2017 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2017 VMware, Inc.
 */
package io.micrometer.benchmark.tracer;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.openjdk.jmh.infra.Blackhole;

interface MicrometerTracingBenchmarks {

    default void micrometerTracing(Tracer tracer, int childSpanCount, Blackhole blackhole) {
        Span parentSpan = tracer.nextSpan().name("parent-span").start();
        for (int i = 0; i < childSpanCount; i++) {
            Span span = tracer.nextSpan(parentSpan).name("new-span" + i);
            span.start().tag("key", "value").event("event").end();
            blackhole.consume(span);
        }
        parentSpan.end();
        blackhole.consume(parentSpan);
    }

    default Span micrometerTracingNewSpan(Tracer tracer) {
        Span span = tracer.nextSpan().name("child-span").start();
        span.end();
        return span;
    }

    default void micrometerTracingWithScope(Tracer tracer, int childSpanCount, Blackhole blackhole) {
        Span parentSpan = tracer.nextSpan().name("parent-span").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(parentSpan)) {
            for (int i = 0; i < childSpanCount; i++) {
                Span childSpan = tracer.nextSpan().name("new-span" + i).start();
                try (Tracer.SpanInScope ws2 = tracer.withSpan(childSpan)) {
                    childSpan.tag("key", "value").event("event");
                }
                childSpan.end();
            }
        }
        parentSpan.end();
        blackhole.consume(parentSpan);
    }

}
