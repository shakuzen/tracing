/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.*;
import io.micrometer.tracing.CurrentTraceContext.Scope;

/**
 * A test implementation of a span in scope.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class SimpleSpanInScope implements Tracer.SpanInScope {

    private volatile boolean closed;

    private final ThreadLocal<SpanAndScope> scopedSpans;

    private final SpanAndScope spanAndScope;

    private final SpanAndScope previousSpanAndScope;

    private final CurrentTraceContext.Scope scope;

    /**
     * Creates a new instance of {@link SimpleSpanInScope}.
     * @param span span
     * @param scopedSpans scoped spans
     * @deprecated use {@link #SimpleSpanInScope(Scope)}
     */
    @Deprecated
    public SimpleSpanInScope(Span span, ThreadLocal<SpanAndScope> scopedSpans) {
        this.scopedSpans = scopedSpans;
        this.spanAndScope = new SpanAndScope(span, this);
        this.previousSpanAndScope = scopedSpans.get();
        this.scope = () -> {
            SpanAndScope current = this.scopedSpans.get();
            if (current != null && current != this.spanAndScope) {
                throw new IllegalStateException("\nTrying to close scope for span \n\n[" + span
                        + "] \n\nbut current span in scope is \n\n[" + current.getSpan() + "]");
            }
            this.scopedSpans.set(this.previousSpanAndScope);
        };
        if (span != null) {
            this.scopedSpans.set(this.spanAndScope);
        }
        else {
            this.scopedSpans.remove();
        }
    }

    /**
     * Creates a new instance of {@link SimpleSpanInScope}.
     * @param scope current trace context scope
     */
    public SimpleSpanInScope(CurrentTraceContext.Scope scope) {
        this.scopedSpans = null;
        this.spanAndScope = null;
        this.previousSpanAndScope = null;
        this.scope = scope;
    }

    @Override
    public void close() {
        this.closed = true;
        this.scope.close();
    }

    /**
     * @return was scoped closed?
     */
    public boolean isClosed() {
        return closed;
    }

}
