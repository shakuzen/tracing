/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.http.HttpClientRequest;
import io.micrometer.tracing.http.HttpClientResponse;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.http.HttpClientHandler;

/**
 * A test implementation of a http client handler.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public class SimpleHttpClientHandler implements HttpClientHandler {

    private final SimpleTracer simpleTracer;

    private volatile boolean receiveHandled;

    /**
     * Creates a new instance of {@link SimpleHttpClientHandler}.
     * @param simpleTracer simple tracer
     */
    public SimpleHttpClientHandler(SimpleTracer simpleTracer) {
        this.simpleTracer = simpleTracer;
    }

    @Override
    public Span handleSend(HttpClientRequest request) {
        return this.simpleTracer.nextSpan().start();
    }

    @Override
    public Span handleSend(HttpClientRequest request, TraceContext parent) {
        return this.simpleTracer.nextSpan().start();
    }

    @Override
    public void handleReceive(HttpClientResponse response, Span span) {
        span.end();
        this.receiveHandled = true;
    }

    /**
     * @return Was the handle receive method called?
     */
    public boolean isReceiveHandled() {
        return this.receiveHandled;
    }

}
