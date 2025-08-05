/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.common.lang.Nullable;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * Abstract response type used for parsing and sampling. Represents an HTTP response.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.10.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface HttpResponse extends Response {

    @Nullable
    @Override
    default HttpRequest request() {
        return null;
    }

    /**
     * Returns an HTTP method.
     * @return an HTTP method
     */
    @Nullable
    default String method() {
        HttpRequest request = request();
        return request != null ? request.method() : null;
    }

    /**
     * Returns an expression such as "/items/:itemId" representing an application
     * endpoint, conventionally associated with the tag key "http.route". If no route
     * matched, "" (empty string) is returned. {@code null} indicates this instrumentation
     * doesn't understand http routes.
     * @return an HTTP route or {@code null} if not set
     */
    @Nullable
    default String route() {
        HttpRequest request = request();
        return request != null ? request.route() : null;
    }

    /**
     * Returns the HTTP status code.
     * @return an HTTP status code or zero if unreadable
     */
    int statusCode();

    /**
     * Returns the header value.
     * @param header header name
     * @return an HTTP header or {@code null} if not set.
     */
    default String header(String header) {
        return null;
    }

}
