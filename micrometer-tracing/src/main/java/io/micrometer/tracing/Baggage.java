/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

/**
 * Inspired by OpenZipkin Brave's {@code BaggageField}. Since some tracer implementations
 * require a scope to be wrapped around baggage, baggage must be closed so that the scope
 * does not leak, see {@link BaggageInScope}. Some tracer implementations make baggage
 * immutable (e.g. OpenTelemetry), so when the value gets updated they might create new
 * scope (others will return the same one - e.g. OpenZipkin Brave).
 * <p>
 * Represents a single baggage entry.
 *
 * @author Marcin Grzejszczak
 * @author Jonatan Ivanov
 * @since 1.0.0
 */
public interface Baggage extends BaggageView {

    /**
     * A noop implementation.
     */
    Baggage NOOP = new Baggage() {
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
        public Baggage set(String value) {
            return this;
        }

        @Override
        public Baggage set(TraceContext traceContext, String value) {
            return this;
        }

        @Override
        public BaggageInScope makeCurrent() {
            return BaggageInScope.NOOP;
        }

        @Override
        public BaggageInScope makeCurrent(String value) {
            return BaggageInScope.NOOP;
        }

        @Override
        public BaggageInScope makeCurrent(TraceContext traceContext, String value) {
            return BaggageInScope.NOOP;
        }
    };

    /**
     * Sets the baggage value.
     * @param value to set
     * @return itself
     * @deprecated use {@link Baggage#makeCurrent(String)}
     */
    @Deprecated
    Baggage set(String value);

    /**
     * Sets the baggage value for the given {@link TraceContext}.
     * @param traceContext context containing baggage
     * @param value to set
     * @return itself
     * @deprecated use {@link Baggage#makeCurrent(TraceContext, String)}
     */
    @Deprecated
    Baggage set(TraceContext traceContext, String value);

    /**
     * Sets the current baggage in scope.
     * @return a {@link BaggageInScope} instance
     */
    BaggageInScope makeCurrent();

    /**
     * Sets the current baggage in scope with given value.
     * @param value to set
     * @return a {@link BaggageInScope} instance
     */
    default BaggageInScope makeCurrent(String value) {
        return set(value).makeCurrent();
    }

    /**
     * Sets the current baggage in scope with given value.
     * @param traceContext context containing baggage
     * @param value to set
     * @return a {@link BaggageInScope} instance
     */
    default BaggageInScope makeCurrent(TraceContext traceContext, String value) {
        return set(traceContext, value).makeCurrent();
    }

}
