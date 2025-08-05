/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.TraceContext;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class SimpleTraceContextBuilderTests {

    @Test
    void should_build_trace_context_through_builder_with_default_values() {
        TraceContext traceContext = new SimpleTraceContextBuilder().build();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(traceContext.traceId()).isEmpty();
            softAssertions.assertThat(traceContext.spanId()).isEmpty();
            softAssertions.assertThat(traceContext.parentId()).isEmpty();
            softAssertions.assertThat(traceContext.sampled()).isFalse();
        });
    }

    @Test
    void should_build_trace_context_through_builder_with_overridden_values() {
        TraceContext traceContext = new SimpleTraceContextBuilder().traceId("1")
            .spanId("2")
            .parentId("3")
            .sampled(true)
            .build();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(traceContext.traceId()).isEqualTo("1");
            softAssertions.assertThat(traceContext.spanId()).isEqualTo("2");
            softAssertions.assertThat(traceContext.parentId()).isEqualTo("3");
            softAssertions.assertThat(traceContext.sampled()).isTrue();
        });
    }

}
