/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.internal.EncodingUtils;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A test implementation of a trace context.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class SimpleTraceContext implements TraceContext {

    private static final Random random = new Random();

    private volatile String traceId = "";

    private volatile String parentId = "";

    private volatile String spanId = "";

    private volatile Boolean sampled = false;

    private Map<String, String> baggageFromParent = new ConcurrentHashMap<>();

    @Override
    public String traceId() {
        return this.traceId;
    }

    @Override
    public String parentId() {
        return this.parentId;
    }

    @Override
    public String spanId() {
        return this.spanId;
    }

    @Override
    public Boolean sampled() {
        return this.sampled;
    }

    /**
     * Sets the trace id.
     * @param traceId trace id
     */
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * Sets the parent span id.
     * @param parentId parent span id
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Sets the span id.
     * @param spanId span id
     */
    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    /**
     * Sets the sampling decision.
     * @param sampled sampled, or not?
     */
    public void setSampled(Boolean sampled) {
        this.sampled = sampled;
    }

    String generateId() {
        return EncodingUtils.fromLong(random.nextLong());
    }

    @Override
    public String toString() {
        return "SimpleTraceContext{" + "traceId='" + traceId + '\'' + ", parentId='" + parentId + '\'' + ", spanId='"
                + spanId + '\'' + ", sampled=" + sampled + '}';
    }

    void addParentBaggage(Map<String, String> baggageFromParent) {
        this.baggageFromParent.putAll(baggageFromParent);
    }

    Map<String, String> baggageFromParent() {
        return this.baggageFromParent;
    }

}
