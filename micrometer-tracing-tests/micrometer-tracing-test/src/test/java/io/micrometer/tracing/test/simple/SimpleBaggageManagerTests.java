/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class SimpleBaggageManagerTests {

    SimpleTracer tracer = new SimpleTracer();

    SimpleBaggageManager manager = new SimpleBaggageManager(tracer);

    @Test
    void should_create_baggage() {
        SimpleSpan simpleSpan = tracer.nextSpan();
        try (Tracer.SpanInScope scope = tracer.withSpan(simpleSpan.start())) {
            try (BaggageInScope baggage = manager.createBaggageInScope("foo", "bar")) {
                then(manager.getBaggage("foo").get()).isEqualTo("bar");
                then(manager.getBaggage(simpleSpan.context(), "foo").get()).isEqualTo("bar");
                then(manager.getAllBaggage()).containsEntry("foo", "bar");
            }
        }
        then(manager.getBaggage("foo").get()).isNull();
        then(manager.getAllBaggage()).isEmpty();
    }

}
