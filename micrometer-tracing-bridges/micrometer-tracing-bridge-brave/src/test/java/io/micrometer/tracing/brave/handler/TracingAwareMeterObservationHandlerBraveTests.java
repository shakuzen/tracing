/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.brave.handler;

import brave.Tracing;
import brave.test.TestSpanHandler;
import io.micrometer.common.lang.NonNull;
import io.micrometer.core.instrument.observation.MeterObservationHandler;
import io.micrometer.observation.Observation;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import io.micrometer.tracing.handler.TracingAwareMeterObservationHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that use {@link BraveTracer} for {@link TracingAwareMeterObservationHandler}.
 *
 * @author Jonatan Ivanov
 */
class TracingAwareMeterObservationHandlerBraveTests {

    private final Tracing tracing = Tracing.newBuilder().addSpanHandler(new TestSpanHandler()).build();

    private final Tracer tracer = new BraveTracer(tracing.tracer(),
            new BraveCurrentTraceContext(tracing.currentTraceContext()), new BraveBaggageManager());

    private final TestMeterObservationHandler delegate = new TestMeterObservationHandler(tracer);

    private final TracingAwareMeterObservationHandler<Observation.Context> handler = new TracingAwareMeterObservationHandler<>(
            delegate, tracer);

    @AfterEach
    void cleanup() {
        tracing.close();
    }

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
