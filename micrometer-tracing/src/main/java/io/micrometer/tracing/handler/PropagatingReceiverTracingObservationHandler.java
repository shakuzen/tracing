/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.handler;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.observation.Observation;
import io.micrometer.observation.transport.ReceiverContext;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;

import java.net.URI;

/**
 * A {@link TracingObservationHandler} called when receiving occurred - e.g. of messages
 * or http requests.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PropagatingReceiverTracingObservationHandler<T extends ReceiverContext>
        implements TracingObservationHandler<T> {

    private static final InternalLogger log = InternalLoggerFactory
        .getInstance(PropagatingReceiverTracingObservationHandler.class);

    private final Tracer tracer;

    private final Propagator propagator;

    /**
     * Creates a new instance of {@link PropagatingReceiverTracingObservationHandler}.
     * @param tracer the tracer to use to record events
     * @param propagator the mechanism to propagate tracing information from the carrier
     */
    public PropagatingReceiverTracingObservationHandler(Tracer tracer, Propagator propagator) {
        this.tracer = tracer;
        this.propagator = propagator;
    }

    @Override
    public void onStart(T context) {
        Span.Builder extractedSpan = this.propagator.extract(context.getCarrier(),
                (carrier, key) -> context.getGetter().get(carrier, key));
        extractedSpan.kind(Span.Kind.valueOf(context.getKind().name()));
        if (context.getRemoteServiceName() != null) {
            extractedSpan.remoteServiceName(context.getRemoteServiceName());
        }
        if (context.getRemoteServiceAddress() != null) {
            try {
                URI uri = URI.create(context.getRemoteServiceAddress());
                extractedSpan = extractedSpan.remoteIpAndPort(uri.getHost(), uri.getPort());
            }
            catch (Exception ex) {
                log.warn("Exception [{}], occurred while trying to parse the uri [{}] to host and port.", ex,
                        context.getRemoteServiceAddress());
            }
        }
        getTracingContext(context).setSpan(customizeExtractedSpan(context, extractedSpan).start());
    }

    /**
     * Customizes the extracted span (e.g. you can set the {@link Span.Kind} via
     * {@link Span.Builder#kind(Span.Kind)}).
     * @param context context
     * @param builder span builder
     * @return span builder
     */
    public Span.Builder customizeExtractedSpan(T context, Span.Builder builder) {
        return builder;
    }

    @Override
    public void onError(T context) {
        if (context.getError() != null) {
            getRequiredSpan(context).error(context.getError());
        }
    }

    @Override
    public void onStop(T context) {
        Span span = getRequiredSpan(context);
        tagSpan(context, span);
        customizeReceiverSpan(context, span);
        span.name(context.getContextualName() != null ? context.getContextualName() : context.getName());
        endSpan(context, span);
    }

    /**
     * Allows to customize the receiver span before reporting it.
     * @param context context
     * @param span span to customize
     */
    public void customizeReceiverSpan(T context, Span span) {

    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context instanceof ReceiverContext;
    }

    @Override
    public Tracer getTracer() {
        return this.tracer;
    }

}
