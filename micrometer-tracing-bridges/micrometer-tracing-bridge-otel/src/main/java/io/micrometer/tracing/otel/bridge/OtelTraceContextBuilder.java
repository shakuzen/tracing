/**
 * Copyright © 2025 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2025 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.tracing.TraceContext;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;

/**
 * OpenTelemetry implementation of a {@link TraceContext.Builder}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class OtelTraceContextBuilder implements TraceContext.Builder {

    private String traceId;

    private String parentId;

    private String spanId;

    @Nullable
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
    public TraceContext.Builder sampled(@Nullable Boolean sampled) {
        this.sampled = sampled;
        return this;
    }

    @Override
    public TraceContext build() {
        boolean actualSampled = this.sampled != null && this.sampled;
        if (StringUtils.isNotEmpty(this.parentId)) {
            return new OtelTraceContext(
                    SpanContext.createFromRemoteParent(this.traceId, this.spanId,
                            actualSampled ? TraceFlags.getSampled() : TraceFlags.getDefault(), TraceState.getDefault()),
                    null) {
                @Override
                public String parentId() {
                    return parentId;
                }

                @Override
                public Boolean sampled() {
                    return sampled;
                }
            };
        }
        return new OtelTraceContext(
                SpanContext.create(this.traceId, this.spanId,
                        actualSampled ? TraceFlags.getSampled() : TraceFlags.getDefault(), TraceState.getDefault()),
                null) {
            @Override
            public Boolean sampled() {
                return sampled;
            }
        };
    }

}
