/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class OtelSpanTests {

    ArrayListSpanProcessor arrayListSpanProcessor = new ArrayListSpanProcessor();

    SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
        .setSampler(io.opentelemetry.sdk.trace.samplers.Sampler.alwaysOn())
        .addSpanProcessor(arrayListSpanProcessor)
        .build();

    OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
        .setTracerProvider(sdkTracerProvider)
        .setPropagators(ContextPropagators.create(B3Propagator.injectingSingleHeader()))
        .build();

    io.opentelemetry.api.trace.Tracer otelTracer = openTelemetrySdk.getTracer("io.micrometer.micrometer-tracing");

    @Test
    void should_set_status_to_error_when_recording_exception() {
        OtelSpan otelSpan = new OtelSpan(otelTracer.spanBuilder("foo").startSpan());

        otelSpan.error(new RuntimeException("boom!")).end();

        SpanData poll = arrayListSpanProcessor.spans().poll();
        then(poll.getStatus()).isEqualTo(StatusData.create(StatusCode.ERROR, "boom!"));
        then(poll.getEvents()).hasSize(1);
        then(poll.getEvents().get(0).getAttributes().asMap().values()).containsAnyOf("boom!");
    }

    @Test
    void should_be_equal_when_two_span_delegates_are_equal() {
        Span span = otelTracer.spanBuilder("foo").startSpan();
        OtelSpan otelSpan = new OtelSpan(span);
        OtelSpan otelSpanFromSpanContext = new OtelSpan(
                new SpanFromSpanContext(span, null, new OtelTraceContext(span)));

        then(otelSpan).isEqualTo(otelSpanFromSpanContext);
        then(otelSpanFromSpanContext).isEqualTo(otelSpan);
    }

}
