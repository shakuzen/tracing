/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.tracing.SpanCustomizer;
import io.micrometer.tracing.TraceContext;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * Use this to control the request data recorded.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface HttpRequestParser {

    /**
     * Implement to choose what data from the http request are parsed into the span
     * representing it.
     * @param request current request
     * @param context corresponding trace context
     * @param span customizer for the current span
     */
    void parse(HttpRequest request, TraceContext context, SpanCustomizer span);

}
