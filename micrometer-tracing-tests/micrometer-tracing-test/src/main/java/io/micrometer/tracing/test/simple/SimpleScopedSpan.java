/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.TraceContext;

/**
 * A test implementation of a scoped span.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class SimpleScopedSpan implements ScopedSpan {

    private final SimpleSpan span;

    /**
     * Creates a new instance of {@link SimpleScopedSpan}.
     * @param simpleTracer simple tracer
     */
    public SimpleScopedSpan(SimpleTracer simpleTracer) {
        this.span = simpleTracer.nextSpan().start();
    }

    @Override
    public boolean isNoop() {
        return this.span.isNoop();
    }

    @Override
    public TraceContext context() {
        return new SimpleTraceContext();
    }

    @Override
    public ScopedSpan name(String name) {
        this.span.name(name);
        return this;
    }

    @Override
    public ScopedSpan tag(String key, String value) {
        this.span.tag(key, value);
        return this;
    }

    @Override
    public ScopedSpan event(String value) {
        this.span.event(value);
        return this;
    }

    @Override
    public ScopedSpan error(Throwable throwable) {
        this.span.error(throwable);
        return this;
    }

    @Override
    public void end() {
        this.span.end();
    }

}
