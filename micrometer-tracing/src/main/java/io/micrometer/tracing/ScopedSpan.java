/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

/**
 * Represents the "current span" until {@link ScopedSpan#end()} ()} is called.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface ScopedSpan {

    /**
     * A noop implementation.
     */
    ScopedSpan NOOP = new ScopedSpan() {
        @Override
        public boolean isNoop() {
            return true;
        }

        @Override
        public TraceContext context() {
            return TraceContext.NOOP;
        }

        @Override
        public ScopedSpan name(String name) {
            return this;
        }

        @Override
        public ScopedSpan tag(String key, String value) {
            return this;
        }

        @Override
        public ScopedSpan event(String value) {
            return this;
        }

        @Override
        public ScopedSpan error(Throwable throwable) {
            return this;
        }

        @Override
        public void end() {

        }
    };

    /**
     * @return {@code true} when no recording is done and nothing is reported to an
     * external system. However, this span should still be injected into outgoing
     * requests. Use this flag to avoid performing expensive computation.
     */
    boolean isNoop();

    /**
     * @return {@link TraceContext} corresponding to this span.
     */
    TraceContext context();

    /**
     * Sets a name on this span.
     * @param name name to set on the span
     * @return this span
     */
    ScopedSpan name(String name);

    /**
     * Sets a tag on this span.
     * @param key tag key
     * @param value tag value
     * @return this span
     */
    ScopedSpan tag(String key, String value);

    /**
     * Sets an event on this span.
     * @param value event name to set on the span
     * @return this span
     */
    ScopedSpan event(String value);

    /**
     * Records an exception for this span.
     * @param throwable to record
     * @return this span
     */
    ScopedSpan error(Throwable throwable);

    /**
     * Ends the span. The span gets stopped and recorded if not noop.
     */
    void end();

}
