/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.common.lang.Nullable;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * This standardizes a way to instrument http clients, particularly in a way that
 * encourages use of portable customizations via {@link HttpRequestParser} and
 * {@link HttpResponseParser}.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface HttpClientHandler {

    /**
     * Starts the client span after assigning it a name and tags. This injects the trace
     * context onto the request before returning.
     * <p>
     * Call this before sending the request on the wire.
     * @param request to inject the tracing context with
     * @return client side span
     */
    Span handleSend(HttpClientRequest request);

    /**
     * Same as {@link #handleSend(HttpClientRequest)} but with an explicit parent
     * {@link TraceContext}.
     * @param request to inject the tracing context with
     * @param parent {@link TraceContext} that is to be the client side span's parent
     * @return client side span
     */
    Span handleSend(HttpClientRequest request, @Nullable TraceContext parent);

    /**
     * Finishes the client span after assigning it tags according to the response or
     * error.
     * @param response the HTTP response
     * @param span span to be ended
     */
    void handleReceive(HttpClientResponse response, Span span);

}
