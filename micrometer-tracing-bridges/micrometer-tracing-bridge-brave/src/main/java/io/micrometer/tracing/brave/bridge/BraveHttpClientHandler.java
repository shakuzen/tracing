/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.tracing.http.HttpClientRequest;
import io.micrometer.tracing.http.HttpClientResponse;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.http.HttpClientHandler;

/**
 * Brave implementation of a {@link HttpClientHandler}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public class BraveHttpClientHandler implements HttpClientHandler {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(BraveHttpClientHandler.class);

    final brave.http.HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> delegate;

    /**
     * Creates a new instance of {@link BraveHttpClientHandler}.
     * @param delegate Brave version of a {@link HttpClientHandler}
     */
    public BraveHttpClientHandler(
            brave.http.HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> delegate) {
        this.delegate = delegate;
        DeprecatedClassLogger.logWarning(getClass());
    }

    @Override
    public Span handleSend(HttpClientRequest request) {
        DeprecatedClassLogger.logWarning(getClass());
        return BraveSpan.fromBrave(this.delegate.handleSend(BraveHttpClientRequest.toBrave(request)));
    }

    @Override
    public Span handleSend(HttpClientRequest request, TraceContext parent) {
        DeprecatedClassLogger.logWarning(getClass());
        brave.Span span = this.delegate.handleSendWithParent(BraveHttpClientRequest.toBrave(request),
                BraveTraceContext.toBrave(parent));
        if (!span.isNoop()) {
            span.remoteIpAndPort(request.remoteIp(), request.remotePort());
        }
        return BraveSpan.fromBrave(span);
    }

    @Override
    public void handleReceive(HttpClientResponse response, Span span) {
        DeprecatedClassLogger.logWarning(getClass());
        if (response == null) {
            if (log.isDebugEnabled()) {
                log.debug("Response is null, will not handle receiving of span [" + span + "]");
            }
            return;
        }
        this.delegate.handleReceive(BraveHttpClientResponse.toBrave(response), BraveSpan.toBrave(span));
    }

}
