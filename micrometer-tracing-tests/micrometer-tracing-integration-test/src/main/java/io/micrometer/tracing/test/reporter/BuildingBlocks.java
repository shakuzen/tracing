/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.reporter;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.propagation.Propagator;

import java.util.Deque;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Building blocks for reporters and tracers.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public interface BuildingBlocks {

    /**
     * Returns a {@link Tracer}.
     * @return tracer
     */
    Tracer getTracer();

    /**
     * Returns a {@link Propagator}.
     * @return propagator
     */
    Propagator getPropagator();

    /**
     * Returns a collection of default {@link ObservationHandler} customizers.
     * @return customizers
     */
    BiConsumer<BuildingBlocks, Deque<ObservationHandler<? extends Observation.Context>>> getCustomizers();

    /**
     * Returns a list of finished spans.
     * @return finished spans
     */
    List<FinishedSpan> getFinishedSpans();

}
