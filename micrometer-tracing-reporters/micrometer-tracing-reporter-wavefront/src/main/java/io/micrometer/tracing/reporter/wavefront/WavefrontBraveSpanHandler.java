/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.reporter.wavefront;

import java.io.Closeable;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import io.micrometer.tracing.brave.bridge.BraveFinishedSpan;
import io.micrometer.tracing.brave.bridge.BraveTraceContext;

/**
 * A {@link SpanHandler} that sends spans to Wavefront.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class WavefrontBraveSpanHandler extends SpanHandler implements Runnable, Closeable {

    private final WavefrontSpanHandler spanHandler;

    /**
     * Creates a new instance of {@link WavefrontBraveSpanHandler}.
     * @param spanHandler wavefront span handler
     */
    public WavefrontBraveSpanHandler(WavefrontSpanHandler spanHandler) {
        this.spanHandler = spanHandler;
    }

    @Override
    public boolean end(TraceContext context, MutableSpan span, Cause cause) {
        return spanHandler.end(BraveTraceContext.fromBrave(context), BraveFinishedSpan.fromBrave(span));
    }

    @Override
    public void close() {
        this.spanHandler.close();
    }

    @Override
    public void run() {
        this.spanHandler.run();
    }

}
