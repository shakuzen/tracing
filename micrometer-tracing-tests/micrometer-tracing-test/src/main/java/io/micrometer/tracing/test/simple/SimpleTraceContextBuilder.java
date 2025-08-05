/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.TraceContext;

/**
 * A test implementation of the trace context builder.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class SimpleTraceContextBuilder implements TraceContext.Builder {

    private String traceId;

    private String parentId;

    private String spanId;

    private Boolean sampled;

    @Override
    public TraceContext.Builder traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    @Override
    public TraceContext.Builder parentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public TraceContext.Builder spanId(String spanId) {
        this.spanId = spanId;
        return this;
    }

    @Override
    public TraceContext.Builder sampled(Boolean sampled) {
        this.sampled = sampled;
        return this;
    }

    @Override
    public TraceContext build() {
        SimpleTraceContext traceContext = new SimpleTraceContext();
        if (this.traceId != null) {
            traceContext.setTraceId(this.traceId);
        }
        if (this.spanId != null) {
            traceContext.setSpanId(this.spanId);
        }
        if (this.parentId != null) {
            traceContext.setParentId(this.parentId);
        }
        if (this.sampled != null) {
            traceContext.setSampled(this.sampled);
        }
        return traceContext;
    }

}
