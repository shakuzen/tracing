/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.common.util.StringUtils;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import org.slf4j.MDC;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link EventListener} to set baggage entries on {@link MDC}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class Slf4JBaggageEventListener implements EventListener {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(Slf4JBaggageEventListener.class);

    private final List<String> lowerCaseCorrelationFields;

    private final List<String> correlationFields;

    /**
     * Creates a new instance of {@link Slf4JBaggageEventListener}.
     * @param correlationFields correlation fields
     */
    public Slf4JBaggageEventListener(List<String> correlationFields) {
        this.lowerCaseCorrelationFields = correlationFields.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        this.correlationFields = correlationFields;
    }

    private void onScopeAttached(EventPublishingContextWrapper.ScopeAttachedEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("Got scope attached event [" + event + "]");
        }
        Span span = event.getSpan();
        if (event.getBaggage() != null && span != null && span.getSpanContext().isValid()) {
            putEntriesIntoMdc(event.getBaggage());
        }
    }

    private void onScopeRestored(EventPublishingContextWrapper.ScopeRestoredEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("Got scope restored event [" + event + "]");
        }
        if (event.getBaggage() != null) {
            putEntriesIntoMdc(event.getBaggage());
        }
    }

    private void putEntriesIntoMdc(Baggage baggage) {
        baggage.forEach((key, baggageEntry) -> {
            if (StringUtils.isNotBlank(baggageEntry.getValue())
                    && lowerCaseCorrelationFields.contains(key.toLowerCase())) {
                MDC.put(key, baggageEntry.getValue());
            }
        });
    }

    private void onScopeClosed(EventPublishingContextWrapper.ScopeClosedEvent event) {
        if (log.isTraceEnabled()) {
            log.trace("Got scope closed event [" + event + "]");
        }
        correlationFields.forEach(MDC::remove);
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
