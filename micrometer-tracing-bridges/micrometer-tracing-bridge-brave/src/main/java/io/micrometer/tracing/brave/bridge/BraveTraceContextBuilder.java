/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.internal.EncodingUtils;

/**
 * Brave implementation of a {@link TraceContext.Builder}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
class BraveTraceContextBuilder implements TraceContext.Builder {

    brave.propagation.TraceContext.Builder delegate = brave.propagation.TraceContext.newBuilder();

    @Override
    public TraceContext.Builder traceId(String traceId) {
        long[] fromString = EncodingUtils.fromString(traceId);
        if (fromString.length == 2) {
            this.delegate.traceIdHigh(fromString[0]);
            this.delegate.traceId(fromString[1]);
        }
        else {
            this.delegate.traceId(fromString[0]);
        }
        return this;
    }

    @Override
    public TraceContext.Builder parentId(String traceId) {
        long[] fromString = EncodingUtils.fromString(traceId);
        this.delegate.parentId(fromString[fromString.length == 2 ? 1 : 0]);
        return this;
    }

    @Override
    public TraceContext.Builder spanId(String spanId) {
        long[] fromString = EncodingUtils.fromString(spanId);
        this.delegate.spanId(fromString[fromString.length == 2 ? 1 : 0]);
        return this;
    }

    @Override
    public TraceContext.Builder sampled(Boolean sampled) {
        this.delegate.sampled(sampled);
        return this;
    }

    @Override
    public TraceContext build() {
        brave.propagation.TraceContext context = this.delegate.build();
        return BraveTraceContext.fromBrave(context);
    }

}
