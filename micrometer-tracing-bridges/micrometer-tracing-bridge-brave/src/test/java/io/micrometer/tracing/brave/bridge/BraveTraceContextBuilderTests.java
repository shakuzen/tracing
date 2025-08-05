/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.TraceContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class BraveTraceContextBuilderTests {

    @Test
    void should_set_trace_context_for_64_bit() {
        BraveTraceContextBuilder builder = new BraveTraceContextBuilder();

        TraceContext traceContext = builder.parentId("7c6239a5ad0a4287")
            .spanId("caff89f7f0f229dd")
            .traceId("596e1787feb11040")
            .sampled(true)
            .build();

        then(traceContext.parentId()).isEqualTo("7c6239a5ad0a4287");
        then(traceContext.spanId()).isEqualTo("caff89f7f0f229dd");
        then(traceContext.traceId()).isEqualTo("596e1787feb11040");
        then(traceContext.sampled()).isTrue();
    }

    @Test
    void should_set_trace_context_for_128_bit() {
        BraveTraceContextBuilder builder = new BraveTraceContextBuilder();

        TraceContext traceContext = builder.parentId("00000000000000007c6239a5ad0a4287")
            .spanId("0000000000000000caff89f7f0f229dd")
            .traceId("596e1787feb11040caff89f7f0f229dd")
            .sampled(true)
            .build();

        then(traceContext.parentId()).isEqualTo("7c6239a5ad0a4287");
        then(traceContext.spanId()).isEqualTo("caff89f7f0f229dd");
        then(traceContext.traceId()).isEqualTo("596e1787feb11040caff89f7f0f229dd");
        then(traceContext.sampled()).isTrue();
    }

}
