/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.opentelemetry.api.trace.Span;
import org.slf4j.MDC;

/**
 * Reacts to events with updating of Slf4j's {@link MDC}.
 *
 * @since 1.0.0
 */
public class Slf4JEventListener implements EventListener {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(Slf4JEventListener.class);

    private static final String DEFAULT_TRACE_ID_KEY = "traceId";

    private static final String DEFAULT_SPAN_ID_KEY = "spanId";

    private final String traceIdKey;

    private final String spanIdKey;

    public Slf4JEventListener() {
        this(DEFAULT_TRACE_ID_KEY, DEFAULT_SPAN_ID_KEY);
    }

    /**
     * @param traceIdKey custom traceId Key
     * @param spanIdKey custom spanId Key
     * @since 1.1.0
     */
    public Slf4JEventListener(String traceIdKey, String spanIdKey) {
        this.traceIdKey = traceIdKey;
        this.spanIdKey = spanIdKey;
    }

    private void onScopeAttached(EventPublishingContextWrapper.ScopeAttachedEvent event) {
        log.trace("Got scope changed event [{}]", event);
        Span span = event.getSpan();
        if (span != null) {
            MDC.put(traceIdKey, span.getSpanContext().getTraceId());
            MDC.put(spanIdKey, span.getSpanContext().getSpanId());
        }
    }

    private void onScopeRestored(EventPublishingContextWrapper.ScopeRestoredEvent event) {
        log.trace("Got scope restored event [{}]", event);
        Span span = event.getSpan();
        if (span != null) {
            MDC.put(traceIdKey, span.getSpanContext().getTraceId());
            MDC.put(spanIdKey, span.getSpanContext().getSpanId());
        }
    }

    private void onScopeClosed(EventPublishingContextWrapper.ScopeClosedEvent event) {
        log.trace("Got scope closed event [{}]", event);
        MDC.remove(traceIdKey);
        MDC.remove(spanIdKey);
    }

    @Override
    public void onEvent(Object event) {
        if (event instanceof EventPublishingContextWrapper.ScopeAttachedEvent) {
            onScopeAttached((EventPublishingContextWrapper.ScopeAttachedEvent) event);
        }
        else if (event instanceof EventPublishingContextWrapper.ScopeClosedEvent) {
            onScopeClosed((EventPublishingContextWrapper.ScopeClosedEvent) event);
        }
        else if (event instanceof EventPublishingContextWrapper.ScopeRestoredEvent) {
            onScopeRestored((EventPublishingContextWrapper.ScopeRestoredEvent) event);
        }
    }

}
