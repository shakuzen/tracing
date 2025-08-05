/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.SpanCustomizer;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.http.HttpRequest;
import io.micrometer.tracing.http.HttpRequestParser;

/**
 * Brave implementation of a {@link HttpRequestParser}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public class BraveHttpRequestParser implements HttpRequestParser {

    final brave.http.HttpRequestParser delegate;

    /**
     * Creates a new version of a {@link BraveHttpRequestParser}.
     * @param delegate Brave version of {@link HttpRequestParser}
     */
    public BraveHttpRequestParser(brave.http.HttpRequestParser delegate) {
        this.delegate = delegate;
    }

    /**
     * Converts from Tracing to Brave.
     * @param parser API parser
     * @return Brave version of the parser
     */
    public static brave.http.HttpRequestParser toBrave(HttpRequestParser parser) {
        if (parser instanceof BraveHttpRequestParser) {
            return ((BraveHttpRequestParser) parser).delegate;
        }
        return (request, context, span) -> parser.parse(BraveHttpRequest.fromBrave(request),
                BraveTraceContext.fromBrave(context), BraveSpanCustomizer.fromBrave(span));
    }

    @Override
    public void parse(HttpRequest request, TraceContext context, SpanCustomizer span) {
        this.delegate.parse(BraveHttpRequest.toBrave(request), BraveTraceContext.toBrave(context),
                BraveSpanCustomizer.toBrave(span));
    }

}
