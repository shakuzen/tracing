/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

import io.micrometer.common.lang.Nullable;

/**
 * Inspired by OpenZipkin Brave's {@code BaggageField}. Represents a single, immutable
 * baggage entry.
 *
 * @author Marcin Grzejszczak
 * @author Jonatan Ivanov
 * @since 1.0.0
 */
public interface BaggageView {

    /**
     * A noop implementation.
     */
    BaggageView NOOP = new BaggageView() {
        @Override
        public String name() {
            return null;
        }

        @Override
        public String get() {
            return null;
        }

        @Override
        public String get(TraceContext traceContext) {
            return null;
        }
    };

    /**
     * Retrieves baggage name.
     * @return name of the baggage entry
     */
    String name();

    /**
     * Retrieves baggage value.
     * @return value of the baggage entry or {@code null} if not set.
     */
    @Nullable
    String get();

    /**
     * Retrieves baggage value from the given {@link TraceContext}.
     * @param traceContext context containing baggage
     * @return value of the baggage entry or {@code null} if not set.
     */
    @Nullable
    String get(TraceContext traceContext);

}
