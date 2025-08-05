/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.http.HttpServerRequest;
import io.micrometer.tracing.http.HttpServerResponse;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.http.HttpServerHandler;

/**
 * Brave implementation of a {@link HttpServerHandler}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public class BraveHttpServerHandler implements HttpServerHandler {

    final brave.http.HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> delegate;

    /**
     * Creates a new instance of {@link HttpServerHandler}.
     * @param delegate Brave version of {@link HttpServerHandler}
     */
    public BraveHttpServerHandler(
            brave.http.HttpServerHandler<brave.http.HttpServerRequest, brave.http.HttpServerResponse> delegate) {
        this.delegate = delegate;
        DeprecatedClassLogger.logWarning(getClass());
    }

    @Override
    public Span handleReceive(HttpServerRequest request) {
        DeprecatedClassLogger.logWarning(getClass());
        return BraveSpan.fromBrave(this.delegate.handleReceive(BraveHttpServerRequest.toBrave(request)));
    }

    @Override
    public void handleSend(HttpServerResponse response, Span span) {
        DeprecatedClassLogger.logWarning(getClass());
        this.delegate.handleSend(BraveHttpServerResponse.toBrave(response), BraveSpan.toBrave(span));
    }

}
