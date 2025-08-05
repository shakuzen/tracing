/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.handler;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.core.instrument.observation.MeterObservationHandler;
import io.micrometer.observation.Observation;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

/**
 * A {@link MeterObservationHandler} that can wrap another one and makes the tracing data
 * available for it. This handler can be used in cases where the {@code MeterRegistry} or
 * the {@link MeterObservationHandler} itself needs access to the tracing data (e.g.:
 * exemplars).
 *
 * @param <T> type of handler context
 * @author Jonatan Ivanov
 * @since 1.0.0
 */
@NonNullApi
public class TracingAwareMeterObservationHandler<T extends Observation.Context> implements MeterObservationHandler<T> {

    private final MeterObservationHandler<T> delegate;

    private final Tracer tracer;

    /**
     * Creates a new instance of {@link TracingAwareMeterObservationHandler}.
     * @param delegate a {@link MeterObservationHandler} delegate
     * @param tracer tracer
     */
    public TracingAwareMeterObservationHandler(MeterObservationHandler<T> delegate, Tracer tracer) {
        this.delegate = delegate;
        this.tracer = tracer;
    }

    @Override
    public void onStart(T context) {
        this.delegate.onStart(context);
    }

    @Override
    public void onError(T context) {
        this.delegate.onError(context);
    }

    @Override
    public void onEvent(Observation.Event event, T context) {
        this.delegate.onEvent(event, context);
    }

    @Override
    public void onScopeOpened(T context) {
        this.delegate.onScopeOpened(context);
    }

    @Override
    public void onScopeClosed(T context) {
        this.delegate.onScopeClosed(context);
    }

    @Override
    public void onStop(T context) {
        TracingObservationHandler.TracingContext tracingContext = context
            .getRequired(TracingObservationHandler.TracingContext.class);
        Span currentSpan = tracingContext.getSpan();
        if (currentSpan != null) {
            try (CurrentTraceContext.Scope ignored = tracer.currentTraceContext().maybeScope(currentSpan.context())) {
                this.delegate.onStop(context);
            }
        }
        else {
            this.delegate.onStop(context);
        }
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return this.delegate.supportsContext(context);
    }

}
