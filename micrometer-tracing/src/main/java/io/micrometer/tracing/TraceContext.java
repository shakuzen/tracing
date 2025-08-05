/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

import io.micrometer.common.lang.Nullable;

/**
 * Contains trace and span data.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface TraceContext {

    /**
     * A noop implementation.
     */
    TraceContext NOOP = new TraceContext() {
        @Override
        public String traceId() {
            return "";
        }

        @Override
        public String parentId() {
            return null;
        }

        @Override
        public String spanId() {
            return "";
        }

        @Override
        public Boolean sampled() {
            return null;
        }
    };

    /**
     * Trace id.
     * @return trace id of a span
     */
    String traceId();

    /**
     * Parent span id.
     * @return parent span id or {@code null} if one is not set
     */
    @Nullable
    String parentId();

    /**
     * Span id.
     * @return span id
     */
    String spanId();

    /**
     * @return {@code true} when sampled, {@code false} when not sampled and {@code null}
     * when sampling decision should be deferred
     */
    @Nullable
    Boolean sampled();

    /**
     * Builder for {@link TraceContext}.
     *
     * @since 1.0.0
     */
    interface Builder {

        /**
         * A noop implementation.
         */
        Builder NOOP = new Builder() {
            @Override
            public Builder traceId(String traceId) {
                return this;
            }

            @Override
            public Builder parentId(String parentId) {
                return this;
            }

            @Override
            public Builder spanId(String spanId) {
                return this;
            }

            @Override
            public Builder sampled(Boolean sampled) {
                return this;
            }

            @Override
            public TraceContext build() {
                return TraceContext.NOOP;
            }
        };

        /**
         * Sets trace id on the trace context.
         * @param traceId trace id
         * @return this
         */
        TraceContext.Builder traceId(String traceId);

        /**
         * Sets parent id on the trace context.
         * @param parentId parent trace id
         * @return this
         */
        TraceContext.Builder parentId(String parentId);

        /**
         * Sets span id on the trace context.
         * @param spanId span id
         * @return this
         */
        TraceContext.Builder spanId(String spanId);

        /**
         * Sets sampled on the trace context.
         * @param sampled if span is sampled
         * @return this
         */
        TraceContext.Builder sampled(@Nullable Boolean sampled);

        /**
         * Builds the trace context.
         * @return trace context
         */
        TraceContext build();

    }

}
