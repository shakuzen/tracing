/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import java.util.Objects;

import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.TraceContext;

/**
 * Brave implementation of a {@link ScopedSpan}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
class BraveScopedSpan implements ScopedSpan {

    final brave.ScopedSpan span;

    BraveScopedSpan(brave.ScopedSpan span) {
        this.span = span;
    }

    @Override
    public boolean isNoop() {
        return this.span.isNoop();
    }

    @Override
    public TraceContext context() {
        return new BraveTraceContext(this.span.context());
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
        this.span.annotate(value);
        return this;
    }

    @Override
    public ScopedSpan error(Throwable throwable) {
        this.span.error(throwable);
        return this;
    }

    @Override
    public void end() {
        this.span.finish();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BraveScopedSpan that = (BraveScopedSpan) o;
        return Objects.equals(this.span, that.span);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.span);
    }

}
