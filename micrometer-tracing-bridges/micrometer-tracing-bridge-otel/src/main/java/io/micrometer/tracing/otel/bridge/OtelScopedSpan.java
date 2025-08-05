/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.TraceContext;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;

/**
 * OpenTelemetry implementation of a {@link ScopedSpan}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
class OtelScopedSpan implements ScopedSpan {

    final Span span;

    final Scope scope;

    OtelScopedSpan(Span span, Scope scope) {
        this.span = span;
        this.scope = scope;
    }

    @Override
    public boolean isNoop() {
        return !this.span.isRecording();
    }

    @Override
    public TraceContext context() {
        return new OtelTraceContext(this.span);
    }

    @Override
    public ScopedSpan name(String name) {
        this.span.updateName(name);
        return this;
    }

    @Override
    public ScopedSpan tag(String key, String value) {
        this.span.setAttribute(key, value);
        return this;
    }

    @Override
    public ScopedSpan event(String value) {
        this.span.addEvent(value);
        return this;
    }

    @Override
    public ScopedSpan error(Throwable throwable) {
        this.span.recordException(throwable);
        return this;
    }

    @Override
    public void end() {
        this.scope.close();
        this.span.end();
    }

}
