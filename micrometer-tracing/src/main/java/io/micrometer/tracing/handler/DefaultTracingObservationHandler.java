/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.handler;

import io.micrometer.observation.Observation;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

/**
 * TracingRecordingListener that uses the Tracing API to record events.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class DefaultTracingObservationHandler implements TracingObservationHandler<Observation.Context> {

    private final Tracer tracer;

    /**
     * Creates a new instance of {@link DefaultTracingObservationHandler}.
     * @param tracer the tracer to use to record events
     */
    public DefaultTracingObservationHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void onStart(Observation.Context context) {
        Span parentSpan = getParentSpan(context);
        Span childSpan = parentSpan != null ? getTracer().nextSpan(parentSpan) : getTracer().nextSpan();
        childSpan.start();
        getTracingContext(context).setSpan(childSpan);
    }

    @Override
    public void onStop(Observation.Context context) {
        Span span = getRequiredSpan(context);
        span.name(getSpanName(context));
        tagSpan(context, span);
        endSpan(context, span);
    }

    @Override
    public Tracer getTracer() {
        return this.tracer;
    }

}
