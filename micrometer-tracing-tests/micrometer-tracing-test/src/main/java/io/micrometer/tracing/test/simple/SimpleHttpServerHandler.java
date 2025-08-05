/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import io.micrometer.tracing.http.HttpServerRequest;
import io.micrometer.tracing.http.HttpServerResponse;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.http.HttpServerHandler;

/**
 * A test implementation of a http server handler.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public class SimpleHttpServerHandler implements HttpServerHandler {

    private final SimpleTracer simpleTracer;

    private volatile boolean receiveHandled;

    /**
     * Creates a new instance of {@link SimpleHttpServerHandler}.
     * @param simpleTracer simple tracer
     */
    public SimpleHttpServerHandler(SimpleTracer simpleTracer) {
        this.simpleTracer = simpleTracer;
    }

    @Override
    public Span handleReceive(HttpServerRequest request) {
        return this.simpleTracer.nextSpan().start();
    }

    @Override
    public void handleSend(HttpServerResponse response, Span span) {
        span.end();
        this.receiveHandled = true;
    }

    /**
     * @return was the handle receive method called?
     */
    public boolean isReceiveHandled() {
        return this.receiveHandled;
    }

}
