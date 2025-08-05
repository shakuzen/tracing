/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

import java.io.Closeable;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.common.lang.Nullable;

/**
 * Container object for {@link Span} and its corresponding {@link Tracer.SpanInScope}.
 *
 * @author Marcin Grzejszczak
 * @author Arthur Gavlyukovskiy
 * @since 1.0.0
 */
public class SpanAndScope implements Closeable {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(SpanAndScope.class);

    private final Span span;

    private final Tracer.SpanInScope scope;

    /**
     * Creates a new span and scope
     * @param span span
     * @param scope scope
     */
    public SpanAndScope(Span span, @Nullable Tracer.SpanInScope scope) {
        this.span = span;
        this.scope = scope;
    }

    /**
     * Gets the span.
     * @return span
     */
    public Span getSpan() {
        return this.span;
    }

    /**
     * Gets the scope.
     * @return scope
     */
    @Nullable
    public Tracer.SpanInScope getScope() {
        return this.scope;
    }

    @Override
    public String toString() {
        return "SpanAndScope{" + "span=" + this.span + '}';
    }

    @Override
    public void close() {
        if (log.isTraceEnabled()) {
            log.trace("Closing span [" + this.span + "]");
        }
        if (this.scope != null) {
            this.scope.close();
        }
        this.span.end();
    }

}
