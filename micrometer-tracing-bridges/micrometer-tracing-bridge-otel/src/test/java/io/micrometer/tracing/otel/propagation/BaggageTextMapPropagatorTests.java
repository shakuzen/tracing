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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static java.util.Collections.emptyList;

class BaggageTextMapPropagatorTests {

    private static final List<String> REMOTE_FIELDS = Arrays.asList("foo", "foo2");

    private BaggageTextMapPropagator baggageTextMapPropagator;

    private TextMapGetter<Map<String, String>> textMapGetter;

    @BeforeEach
    void setup() {
        baggageTextMapPropagator = new BaggageTextMapPropagator(REMOTE_FIELDS,
                new OtelBaggageManager(new OtelCurrentTraceContext(), REMOTE_FIELDS, emptyList()));
        textMapGetter = textMapGetter(REMOTE_FIELDS);
    }

    @Test
    void should_append_baggage_to_existing_one() {
        Baggage baggage = Baggage.empty().toBuilder().put("foo", "undesired").put("lorem", "ipsum").build();
        Context parent = Context.root().with(baggage);

        Map<String, String> carrier = new HashMap<>();
        carrier.put("foo", "bar");
        carrier.put("foo2", "bar2");

        Context extracted = baggageTextMapPropagator.extract(parent, carrier, textMapGetter);

        Map<String, BaggageEntry> extractedBaggage = Baggage.fromContext(extracted).asMap();
        BDDAssertions.then(extractedBaggage).containsOnlyKeys("foo", "foo2", "lorem");

        BDDAssertions.then(extractedBaggage.get("foo").getValue()).isEqualTo("bar");
        BDDAssertions.then(extractedBaggage.get("foo2").getValue()).isEqualTo("bar2");
        BDDAssertions.then(extractedBaggage.get("lorem").getValue()).isEqualTo("ipsum");
    }

    @Test
    void should_not_mix_baggage_with_current_baggage() {
        Baggage currentBaggage = Baggage.empty()
            .toBuilder()
            .put("current-key", "undesired")
            .put("foo", "undesired")
            .build();
        Context currentContext = Context.root().with(currentBaggage);

        Context extracted = currentContext.wrapSupplier(() -> {
            Map<String, String> carrier = new HashMap<>();
            carrier.put("foo", "bar");

            return baggageTextMapPropagator.extract(Context.root(), carrier, textMapGetter(REMOTE_FIELDS));
        }).get();

        Map<String, BaggageEntry> extractedBaggage = Baggage.fromContext(extracted).asMap();
        BDDAssertions.then(extractedBaggage).doesNotContainKey("current-key");
        BDDAssertions.then(extractedBaggage.get("foo").getValue()).isEqualTo("bar");
    }

    private TextMapGetter<Map<String, String>> textMapGetter(final List<String> remoteFields) {
        return new TextMapGetter<Map<String, String>>() {
            @Override
            public Iterable<String> keys(Map<String, String> carrier) {
                return remoteFields;
            }

            @Override
            public String get(Map<String, String> carrier, String key) {
                return carrier.get(key);
            }
        };
    }

}
