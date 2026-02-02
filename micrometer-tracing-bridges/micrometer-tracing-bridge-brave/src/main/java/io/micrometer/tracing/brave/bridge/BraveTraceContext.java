/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import java.util.Objects;

import io.micrometer.common.lang.Nullable;
import io.micrometer.tracing.TraceContext;

/**
 * Brave implementation of a {@link TraceContext}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class BraveTraceContext implements TraceContext {

    final brave.propagation.TraceContext traceContext;

    /**
     * Creates a new instance of {@link BraveTraceContext}.
     * @param traceContext Brave {@link TraceContext}
     */
    public BraveTraceContext(brave.propagation.TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    /**
     * Converts from Tracing to Brave.
     * @param traceContext Tracing version
     * @return Brave version
     */
    @Nullable
    public static brave.propagation.TraceContext toBrave(@Nullable TraceContext traceContext) {
        if (traceContext == null) {
            return null;
        }
        return ((BraveTraceContext) traceContext).traceContext;
    }

    /**
     * Converts from Brave to Tracing.
     * @param traceContext Brave version
     * @return Tracing version
     */
    public static TraceContext fromBrave(brave.propagation.TraceContext traceContext) {
        return new BraveTraceContext(traceContext);
    }

    @Override
    public String traceId() {
        return this.traceContext.traceIdString();
    }

    @Override
    @Nullable
    public String parentId() {
        return this.traceContext.parentIdString();
    }

    @Override
    public String spanId() {
        return this.traceContext.spanIdString();
    }

    @Override
    @Nullable
    public Boolean sampled() {
        return this.traceContext.sampled();
    }

    @Override
    public String toString() {
        return this.traceContext != null ? this.traceContext.toString() : "null";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BraveTraceContext that = (BraveTraceContext) o;
        return Objects.equals(this.traceContext, that.traceContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.traceContext);
    }

}
