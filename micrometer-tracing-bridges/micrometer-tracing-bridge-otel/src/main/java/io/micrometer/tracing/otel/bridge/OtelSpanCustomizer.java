/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.tracing.SpanCustomizer;
import io.opentelemetry.api.trace.Span;

/**
 * OpenTelemetry implementation of a {@link SpanCustomizer}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class OtelSpanCustomizer implements SpanCustomizer {

    @Override
    public SpanCustomizer name(String name) {
        currentSpan().updateName(name);
        return this;
    }

    private Span currentSpan() {
        return Span.current();
    }

    @Override
    public SpanCustomizer tag(String key, String value) {
        currentSpan().setAttribute(key, value);
        return this;
    }

    @Override
    public SpanCustomizer tag(String key, long value) {
        currentSpan().setAttribute(key, value);
        return this;
    }

    @Override
    public SpanCustomizer tag(String key, double value) {
        currentSpan().setAttribute(key, value);
        return this;
    }

    @Override
    public SpanCustomizer tag(String key, boolean value) {
        currentSpan().setAttribute(key, value);
        return this;
    }

    @Override
    public SpanCustomizer event(String value) {
        currentSpan().addEvent(value);
        return this;
    }

}
