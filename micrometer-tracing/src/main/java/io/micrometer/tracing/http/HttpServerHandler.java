/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.tracing.Span;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * This standardizes a way to instrument http servers, particularly in a way that
 * encourages use of portable customizations via {@link HttpRequestParser} and
 * {@link HttpResponseParser}.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface HttpServerHandler {

    /**
     * Conditionally joins a span, or starts a new trace, depending on if a trace context
     * was extracted from the request. Tags are added before the span is started.
     * @param request HTTP request
     * @return server side span (either joined or a new trace)
     */
    Span handleReceive(HttpServerRequest request);

    /**
     * Finishes the server span after assigning it tags according to the response or
     * error.
     * @param response HTTP response
     * @param span server side span to end
     */
    void handleSend(HttpServerResponse response, Span span);

}
