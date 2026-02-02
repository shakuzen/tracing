/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.otel.handler;

import java.util.Collections;

import io.micrometer.common.lang.NonNull;
import io.micrometer.core.instrument.observation.MeterObservationHandler;
import io.micrometer.observation.Observation;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import io.micrometer.tracing.handler.TracingAwareMeterObservationHandler;
import io.micrometer.tracing.otel.bridge.ArrayListSpanProcessor;
import io.micrometer.tracing.otel.bridge.OtelBaggageManager;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that use {@link OtelTracer} for {@link TracingAwareMeterObservationHandler}.
 *
 * @author Jonatan Ivanov
 */
class TracingAwareMeterObservationHandlerOtelTests {

    private final SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
        .setSampler(io.opentelemetry.sdk.trace.samplers.Sampler.alwaysOn())
        .addSpanProcessor(SimpleSpanProcessor.create(new ArrayListSpanProcessor()))
        .build();

    private final OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
        .setTracerProvider(sdkTracerProvider)
        .setPropagators(ContextPropagators.create(B3Propagator.injectingSingleHeader()))
        .build();

    private final io.opentelemetry.api.trace.Tracer otelTracer = openTelemetrySdk
        .getTracer("io.micrometer.micrometer-tracing");

    private final Tracer tracer = new OtelTracer(otelTracer, new OtelCurrentTraceContext(), event -> {
    }, new OtelBaggageManager(new OtelCurrentTraceContext(), Collections.emptyList(), Collections.emptyList()));

    private final TestMeterObservationHandler delegate = new TestMeterObservationHandler(tracer);

    private final TracingAwareMeterObservationHandler<Observation.Context> handler = new TracingAwareMeterObservationHandler<>(
            delegate, tracer);

    @Test
    void delegateShouldGetTheCurrentSpanAtStop() {
        TestObservationRegistry registry = TestObservationRegistry.create();
        registry.observationConfig()
            .observationHandler(new DefaultTracingObservationHandler(tracer))
            .observationHandler(handler);

        Span span;
        Observation observation = Observation.start("test", registry);
        try (Observation.Scope ignored = observation.openScope()) {
            span = tracer.currentSpan();
        }
        finally {
            observation.stop();
        }

        assertThat(delegate.getCurrentSpan()).isNotNull().isEqualTo(span);
    }

    static class TestMeterObservationHandler implements MeterObservationHandler<Observation.Context> {

        private final Tracer tracer;

        private Span currentSpan;

        TestMeterObservationHandler(Tracer tracer) {
            this.tracer = tracer;
        }

        @Override
        public void onStop(@NonNull Observation.Context context) {
            this.currentSpan = tracer.currentSpan();
        }

        Span getCurrentSpan() {
            return currentSpan;
        }

    }

}
