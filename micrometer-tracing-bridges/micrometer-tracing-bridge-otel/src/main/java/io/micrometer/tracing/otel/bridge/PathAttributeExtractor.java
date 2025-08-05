/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.common.util.StringUtils;
import io.micrometer.tracing.http.HttpRequest;
import io.micrometer.tracing.http.HttpResponse;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.semconv.SemanticAttributes;

/**
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
class PathAttributeExtractor implements AttributesExtractor<HttpRequest, HttpResponse> {

    private static final AttributeKey<String> HTTP_PATH = AttributeKey.stringKey("http.path");

    @Override
    public void onStart(AttributesBuilder attributes, Context parentContext, HttpRequest httpRequest) {
        String path = httpRequest.path();
        if (StringUtils.isNotEmpty(path)) {
            setAttribute(attributes, SemanticAttributes.HTTP_ROUTE, path);
            setAttribute(attributes, HTTP_PATH, path);
        }
    }

    private <T> void setAttribute(AttributesBuilder attributes, AttributeKey<T> key, T value) {
        if (value != null) {
            attributes.put(key, value);
        }
    }

    @Override
    public void onEnd(AttributesBuilder attributes, Context context, HttpRequest httpRequest, HttpResponse httpResponse,
            Throwable error) {

    }

}
