/**
 * Copyright © 2024 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2024 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class OtelTraceContextTests {

    SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
        .setSampler(io.opentelemetry.sdk.trace.samplers.Sampler.alwaysOn())
        .build();

    OpenTelemetrySdkBuilder openTelemetrySdkBuilder = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider);

    @Test
    void should_return_null_when_parent_invalid() {

        try (OpenTelemetrySdk openTelemetrySdk = openTelemetrySdkBuilder.build()) {
            Tracer otelTracer = tracer(openTelemetrySdk);
            Span span = otelTracer.spanBuilder("foo").startSpan();

            OtelTraceContext otelTraceContext = new OtelTraceContext(span);

            then(otelTraceContext.parentId()).isNull();
        }

    }

    @Test
    void should_return_null_when_spans_not_sampled() { // works differently than Brave
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
            .setSampler(io.opentelemetry.sdk.trace.samplers.Sampler.alwaysOff())
            .build();

        OpenTelemetrySdkBuilder openTelemetrySdkBuilder = OpenTelemetrySdk.builder()
            .setTracerProvider(sdkTracerProvider);

        try (OpenTelemetrySdk openTelemetrySdk = openTelemetrySdkBuilder.build()) {
            Tracer otelTracer = tracer(openTelemetrySdk);
            Span parentSpan = otelTracer.spanBuilder("parent").startSpan();
            Span span = otelTracer.spanBuilder("foo")
                .setParent(parentSpan.storeInContext(Context.current()))
                .startSpan();

            OtelTraceContext otelTraceContext = new OtelTraceContext(span);

            then(otelTraceContext.parentId()).isNull();
        }

    }

    @Test
    void should_return_parentid_when_parent_valid() {
        try (OpenTelemetrySdk openTelemetrySdk = openTelemetrySdkBuilder.build()) {
            Tracer otelTracer = tracer(openTelemetrySdk);
            Span parentSpan = otelTracer.spanBuilder("parent").startSpan();
            Span span = otelTracer.spanBuilder("foo")
                .setParent(parentSpan.storeInContext(Context.current()))
                .startSpan();

            OtelTraceContext otelTraceContext = new OtelTraceContext(span);

            then(otelTraceContext.parentId()).isEqualTo(parentSpan.getSpanContext().getSpanId());
        }

    }

    private static Tracer tracer(OpenTelemetrySdk openTelemetrySdk) {
        return openTelemetrySdk.getTracer("io.micrometer.micrometer-tracing");
    }

}
