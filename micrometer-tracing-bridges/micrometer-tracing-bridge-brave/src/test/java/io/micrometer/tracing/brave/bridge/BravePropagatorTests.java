/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import brave.propagation.StrictCurrentTraceContext;
import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class BravePropagatorTests {

    StrictCurrentTraceContext currentTraceContext = new StrictCurrentTraceContext();

    Tracing tracing = Tracing.newBuilder()
        .propagationFactory(micrometerTracingPropagationWithBaggage(b3PropagationFactory()))
        .currentTraceContext(currentTraceContext)
        .build();

    BraveBaggageManager braveBaggageManager = new BraveBaggageManager();

    Tracer tracer = new BraveTracer(tracing.tracer(), new BraveCurrentTraceContext(tracing.currentTraceContext()),
            braveBaggageManager);

    BravePropagator bravePropagator = new BravePropagator(tracing);

    @AfterEach
    void cleanup() {
        tracing.close();
        currentTraceContext.close();
    }

    @Test
    void should_propagate_context_with_trace_and_baggage() {
        Map<String, String> carrier = new HashMap<>();
        carrier.put("b3", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
        carrier.put("foo", "bar");
        Span.Builder extract = bravePropagator.extract(carrier, Map::get);

        Span span = extract.start();

        BaggageInScope baggage = tracer.getBaggage(span.context(), "foo").makeCurrent();
        try {
            BDDAssertions.then(baggage.get(span.context())).isEqualTo("bar");
        }
        finally {
            baggage.close();
        }
    }

    @Test
    void should_propagate_context_with_baggage_only() {
        Map<String, String> carrier = new HashMap<>();
        carrier.put("foo", "bar");
        Span.Builder extract = bravePropagator.extract(carrier, Map::get);

        Span span = extract.start();

        BaggageInScope baggage = tracer.getBaggage(span.context(), "foo").makeCurrent();
        try {
            BDDAssertions.then(baggage.get(span.context())).isEqualTo("bar");
        }
        finally {
            baggage.close();
        }
    }

    private BaggagePropagation.FactoryBuilder b3PropagationFactory() {
        return BaggagePropagation.newFactoryBuilder(
                B3Propagation.newFactoryBuilder().injectFormat(B3Propagation.Format.SINGLE_NO_PARENT).build());
    }

    private Propagation.Factory micrometerTracingPropagationWithBaggage(
            BaggagePropagation.FactoryBuilder factoryBuilder) {
        return factoryBuilder.add(BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create("foo")))
            .build();
    }

}
