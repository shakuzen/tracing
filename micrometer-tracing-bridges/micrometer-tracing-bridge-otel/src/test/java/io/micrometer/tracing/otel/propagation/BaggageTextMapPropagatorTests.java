/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.propagation;

import io.micrometer.tracing.otel.bridge.OtelBaggageManager;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageEntry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class BaggageTextMapPropagatorTests {

    @Test
    void should_append_baggage_to_existing_one() {
        Baggage baggage = Baggage.current().toBuilder().put("foo", "bar").put("baz", "baz2").build();
        Context parent = Context.current().with(baggage);
        List<String> remoteFields = Arrays.asList("foo", "foo2");
        BaggageTextMapPropagator baggageTextMapPropagator = new BaggageTextMapPropagator(remoteFields,
                new OtelBaggageManager(new OtelCurrentTraceContext(), remoteFields, Collections.emptyList()));
        Map<String, String> carrier = new HashMap<>();
        carrier.put("foo", "bar");
        carrier.put("foo2", "bar2");

        Context extracted = baggageTextMapPropagator.extract(parent, carrier, new TextMapGetter<Map<String, String>>() {
            @Override
            public Iterable<String> keys(Map<String, String> carrier) {
                return remoteFields;
            }

            @Override
            public String get(Map<String, String> carrier, String key) {
                return carrier.get(key);
            }
        });

        Map<String, BaggageEntry> newBaggage = Baggage.fromContext(extracted).asMap();
        BDDAssertions.then(newBaggage).containsOnlyKeys("foo", "foo2", "baz");
    }

}
