/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

/**
 * Allows to customize the current span in scope.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface SpanCustomizer {

    /**
     * A noop implementation.
     */
    SpanCustomizer NOOP = new SpanCustomizer() {
        @Override
        public SpanCustomizer name(String name) {
            return this;
        }

        @Override
        public SpanCustomizer tag(String key, String value) {
            return this;
        }

        @Override
        public SpanCustomizer event(String value) {
            return this;
        }
    };

    /**
     * Sets a name on a span.
     * @param name name to set on a span
     * @return this, for chaining
     */
    SpanCustomizer name(String name);

    /**
     * Sets a tag on a span.
     * @param key tag key
     * @param value tag value
     * @return this, for chaining
     */
    SpanCustomizer tag(String key, String value);

    /**
     * Sets a tag on a span.
     * @param key tag key
     * @param value tag value
     * @return this, for chaining
     */
    default SpanCustomizer tag(String key, long value) {
        return tag(key, String.valueOf(value));
    }

    /**
     * Sets a tag on a span.
     * @param key tag key
     * @param value tag value
     * @return this, for chaining
     */
    default SpanCustomizer tag(String key, double value) {
        return tag(key, String.valueOf(value));
    }

    /**
     * Sets a tag on a span.
     * @param key tag key
     * @param value tag value
     * @return this, for chaining
     */
    default SpanCustomizer tag(String key, boolean value) {
        return tag(key, String.valueOf(value));
    }

    /**
     * Sets an event on a span.
     * @param value event name
     * @return this, for chaining
     */
    SpanCustomizer event(String value);

}
