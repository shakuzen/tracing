/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.context.ContextAccessor;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ContextSnapshot;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Predicate;

import static org.assertj.core.api.BDDAssertions.thenNoException;

class SimpleTracerTests {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(SimpleTracerTests.class);

    @Test
    void should_not_break_on_scopes() {
        ObservationRegistry registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(new DefaultTracingObservationHandler(new SimpleTracer()));

        ContextRegistry.getInstance().registerContextAccessor(new ContextAccessor<Observation, Observation>() {
            @Override
            public Class<? extends Observation> readableType() {
                return Observation.class;
            }

            @Override
            public void readValues(Observation sourceContext, Predicate<Object> keyPredicate,
                    Map<Object, Object> readValues) {
                readValues.put(ObservationThreadLocalAccessor.KEY, sourceContext);
            }

            @Override
            public <T> T readValue(Observation sourceContext, Object key) {
                return (T) sourceContext;
            }

            @Override
            public Class<? extends Observation> writeableType() {
                return Observation.class;
            }

            @Override
            public Observation writeValues(Map<Object, Object> valuesToWrite, Observation targetContext) {
                return (Observation) valuesToWrite.get(ObservationThreadLocalAccessor.KEY);
            }
        });

        Observation obs0 = Observation.createNotStarted("observation-0", registry);
        Observation obs1 = Observation.createNotStarted("observation-1", registry);

        thenNoException().isThrownBy(() -> {
            try (Observation.Scope scope = obs0.start().openScope()) {
                try (Observation.Scope scope2 = obs1.start().openScope()) {
                    try (ContextSnapshot.Scope scope3 = ContextSnapshot.setAllThreadLocalsFrom(obs1)) {
                        log.info("hello from here");
                    }
                }
            }
        });
    }

}
