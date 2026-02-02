/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.tracing.*;
import io.micrometer.tracing.otel.propagation.BaggageTextMapPropagator;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class OtelPropagatorTests {

    SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
        .setSampler(io.opentelemetry.sdk.trace.samplers.Sampler.alwaysOn())
        .build();

    OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
        .setTracerProvider(sdkTracerProvider)
        .setPropagators(ContextPropagators.create(B3Propagator.injectingSingleHeader()))
        .build();

    io.opentelemetry.api.trace.Tracer otelTracer = openTelemetrySdk.getTracer("io.micrometer.micrometer-tracing");

    OtelCurrentTraceContext otelCurrentTraceContext = new OtelCurrentTraceContext();

    OtelBaggageManager otelBaggageManager = new OtelBaggageManager(otelCurrentTraceContext, Collections.emptyList(),
            Collections.emptyList());

    ContextPropagators contextPropagators = ContextPropagators
        .create(TextMapPropagator.composite(W3CBaggagePropagator.getInstance(), W3CTraceContextPropagator.getInstance(),
                new BaggageTextMapPropagator(Collections.singletonList("foo"), otelBaggageManager)));

    OtelPropagator otelPropagator = new OtelPropagator(contextPropagators, otelTracer);

    @Test
    void should_propagate_context_with_trace_and_baggage() {
        Map<String, String> carrier = new HashMap<>();
        carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
        carrier.put("baggage", "foo=bar");
        Span.Builder extract = otelPropagator.extract(carrier, Map::get);

        Span span = extract.start();

        try (BaggageInScope baggage = otelBaggageManager.getBaggage(span.context(), "foo").makeCurrent()) {
            BDDAssertions.then(baggage.get(span.context())).isEqualTo("bar");
        }
    }

    @Test
    void should_propagate_context_with_baggage_only() {
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "foo=bar");
        Span.Builder extract = otelPropagator.extract(carrier, Map::get);

        Span span = extract.start();

        try (BaggageInScope baggage = otelBaggageManager.getBaggage(span.context(), "foo").makeCurrent()) {
            BDDAssertions.then(baggage.get(span.context())).isEqualTo("bar");
        }
    }

    @Test
    void should_propagate_context_with_baggage_only_as_field() {
        Map<String, String> carrier = new HashMap<>();
        carrier.put("foo", "bar");
        Span.Builder extract = otelPropagator.extract(carrier, Map::get);

        Span span = extract.start();

        try (BaggageInScope baggage = new OtelBaggageManager(otelCurrentTraceContext, Collections.emptyList(),
                Collections.emptyList())
            .getBaggage(span.context(), "foo")
            .makeCurrent()) {
            BDDAssertions.then(baggage.get(span.context())).isEqualTo("bar");
        }
    }

    @Test
    void should_use_created_child_context_in_scope_instead_of_parent() {
        OtelBaggageManager baggageManager = new OtelBaggageManager(otelCurrentTraceContext, Collections.emptyList(),
                Collections.emptyList());
        OtelTracer tracer = new OtelTracer(otelTracer, otelCurrentTraceContext, Function.identity()::apply,
                baggageManager);

        Map<String, String> carrier = new HashMap<>();
        carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");

        Span extracted = otelPropagator.extract(carrier, Map::get).start();
        String expectedSpanId = extracted.context().spanId();

        try (Tracer.SpanInScope ignored = tracer.withSpan(extracted)) {
            assertThat(tracer.currentSpan()).extracting(Span::context)
                .returns(expectedSpanId, TraceContext::spanId)
                .returns("3e425f2373d89640bde06e8285e7bf88", TraceContext::traceId)
                .returns("9a5fdefae3abb440", TraceContext::parentId);

            assertThat(tracer.currentTraceContext()).isNotNull()
                .extracting(CurrentTraceContext::context)
                .isNotNull()
                .returns(expectedSpanId, TraceContext::spanId)
                .returns("3e425f2373d89640bde06e8285e7bf88", TraceContext::traceId)
                .returns("9a5fdefae3abb440", TraceContext::parentId);
        }
    }

}
