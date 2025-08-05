/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.SpanCustomizer;

/**
 * Brave implementation of a {@link SpanCustomizer}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class BraveSpanCustomizer implements SpanCustomizer {

    private final brave.SpanCustomizer spanCustomizer;

    /**
     * Creates a new instance of {@link BraveSpanCustomizer}.
     * @param spanCustomizer Brave {@link SpanCustomizer}
     */
    public BraveSpanCustomizer(brave.SpanCustomizer spanCustomizer) {
        this.spanCustomizer = spanCustomizer;
    }

    /**
     * Converts from Tracing to Brave.
     * @param spanCustomizer Tracing version
     * @return Brave version
     */
    public static brave.SpanCustomizer toBrave(SpanCustomizer spanCustomizer) {
        return ((BraveSpanCustomizer) spanCustomizer).spanCustomizer;
    }

    /**
     * Converts from Brave to Tracing.
     * @param spanCustomizer Brave version
     * @return Tracing version
     */
    public static SpanCustomizer fromBrave(brave.SpanCustomizer spanCustomizer) {
        return new BraveSpanCustomizer(spanCustomizer);
    }

    @Override
    public SpanCustomizer name(String name) {
        return new BraveSpanCustomizer(this.spanCustomizer.name(name));
    }

    @Override
    public SpanCustomizer tag(String key, String value) {
        return new BraveSpanCustomizer(this.spanCustomizer.tag(key, value));
    }

    @Override
    public SpanCustomizer event(String value) {
        return new BraveSpanCustomizer(this.spanCustomizer.annotate(value));
    }

}
