/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.SpanCustomizer;
import io.micrometer.tracing.Tracer;

/**
 * A test implementation of a span customizer.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class SimpleSpanCustomizer implements SpanCustomizer {

    private final SimpleSpan span;

    private final Tracer tracer;

    /**
     * Creates a new instance of {@link SimpleSpanCustomizer}.
     * @param span simple span
     * @deprecated use {@link SimpleSpanCustomizer(SimpleTracer)}
     */
    @Deprecated
    public SimpleSpanCustomizer(SimpleSpan span) {
        this.span = span;
        this.tracer = Tracer.NOOP;
    }

    /**
     * Creates a new instance of {@link SimpleSpanCustomizer}.
     * @param tracer simple tracer
     */
    public SimpleSpanCustomizer(SimpleTracer tracer) {
        this.span = null;
        this.tracer = tracer;
    }

    private Span currentSpan() {
        Span currentSpan = this.tracer.currentSpan();
        if (currentSpan == null) {
            throw new IllegalStateException("Current span is null");
        }
        return currentSpan;
    }

    @Override
    public SpanCustomizer name(String name) {
        currentSpan().name(name);
        return this;
    }

    @Override
    public SpanCustomizer tag(String key, String value) {
        currentSpan().tag(key, value);
        return this;
    }

    @Override
    public SpanCustomizer event(String value) {
        currentSpan().event(value);
        return this;
    }

}
