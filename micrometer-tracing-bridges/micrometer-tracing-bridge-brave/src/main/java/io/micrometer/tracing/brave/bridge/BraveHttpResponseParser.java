/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.http.HttpResponse;
import io.micrometer.tracing.SpanCustomizer;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.http.HttpResponseParser;

/**
 * Brave implementation of a {@link HttpResponseParser}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public class BraveHttpResponseParser implements HttpResponseParser {

    final brave.http.HttpResponseParser delegate;

    /**
     * Creates a new instance of {@link BraveHttpResponseParser}.
     * @param delegate Brave version of {@link HttpResponseParser}
     */
    public BraveHttpResponseParser(brave.http.HttpResponseParser delegate) {
        this.delegate = delegate;
        DeprecatedClassLogger.logWarning(getClass());
    }

    /**
     * Converts from Tracing to Brave.
     * @param parser API parser
     * @return Brave parser
     */
    public static brave.http.HttpResponseParser toBrave(HttpResponseParser parser) {
        DeprecatedClassLogger.logWarning(BraveHttpRequestParser.class);
        if (parser instanceof BraveHttpResponseParser) {
            return ((BraveHttpResponseParser) parser).delegate;
        }
        return (response, context, span) -> parser.parse(BraveHttpResponse.fromBrave(response),
                BraveTraceContext.fromBrave(context), BraveSpanCustomizer.fromBrave(span));
    }

    @Override
    public void parse(HttpResponse response, TraceContext context, SpanCustomizer span) {
        DeprecatedClassLogger.logWarning(getClass());
        this.delegate.parse(BraveHttpResponse.toBrave(response), BraveTraceContext.toBrave(context),
                BraveSpanCustomizer.toBrave(span));
    }

}
