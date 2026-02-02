/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

import java.io.Closeable;

/**
 * Inspired by OpenZipkin Brave's {@code BaggageField}. Since some tracer implementations
 * require a scope to be wrapped around baggage, baggage must be closed so that the scope
 * does not leak. Some tracer implementations make baggage immutable (e.g. OpenTelemetry),
 * so when the value gets updated they might create new scope (others will return the same
 * one - e.g. OpenZipkin Brave).
 * <p>
 * Represents a single baggage entry.
 *
 * @author Marcin Grzejszczak
 * @author Jonatan Ivanov
 * @since 1.0.0
 */
public interface BaggageInScope extends BaggageView, Closeable {

    /**
     * A noop implementation.
     */
    BaggageInScope NOOP = new BaggageInScope() {
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

        @Override
        public void close() {
        }
    };

    @Override
    void close();

}
