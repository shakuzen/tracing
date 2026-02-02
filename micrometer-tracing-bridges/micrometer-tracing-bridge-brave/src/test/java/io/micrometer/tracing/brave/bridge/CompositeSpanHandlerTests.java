/**
 * Copyright © 2024 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2024 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler.Cause;
import brave.internal.codec.HexCodec;
import brave.propagation.TraceContext;
import io.micrometer.tracing.exporter.TestSpanReporter;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class CompositeSpanHandlerTests {

    @Test
    void should_store_spans_through_test_span_reporter() {
        TestSpanReporter testSpanReporter = new TestSpanReporter();
        TraceContext traceContext = TraceContext.newBuilder().traceId(id()).spanId(id()).build();
        MutableSpan mutableSpan = new MutableSpan(traceContext, null);
        mutableSpan.name("bar");

        boolean success = new CompositeSpanHandler(null, Collections.singletonList(testSpanReporter), null)
            .end(traceContext, mutableSpan, Cause.FINISHED);

        BDDAssertions.then(success).isTrue();
        BDDAssertions.then(testSpanReporter.spans()).hasSize(1);
        BDDAssertions.then(testSpanReporter.poll().getName()).isEqualTo("bar");
    }

    private static long id() {
        return HexCodec.lowerHexToUnsignedLong("ff000000000000000000000000000041");
    }

}
