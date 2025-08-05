/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.common.lang.Nullable;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * Abstract response type used for parsing and sampling. Represents an HTTP request.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.10.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface HttpRequest extends Request {

    /**
     * Returns an HTTP method.
     * @return an HTTP method.
     */
    String method();

    /**
     * Returns an HTTP path.
     * @return an HTTP path or {@code null} if not set.
     */
    @Nullable
    String path();

    /**
     * Returns an expression such as "/items/:itemId" representing an application
     * endpoint, conventionally associated with the tag key "http.route". If no route
     * matched, "" (empty string) is returned. {@code null} indicates this instrumentation
     * doesn't understand http routes.
     * @return an HTTP route or {@code null} if not set
     */
    @Nullable
    default String route() {
        return null;
    }

    /**
     * Returns an HTTP URL.
     * @return an HTTP URL or {@code null} if not set.
     */
    @Nullable
    String url();

    /**
     * Returns a header.
     * @param name header name
     * @return an HTTP header or {@code null} if not set.
     */
    @Nullable
    String header(String name);

    /**
     * Returns a remote IP.
     * @return remote IP for the given connection.
     */
    default String remoteIp() {
        return null;
    }

    /**
     * Returns a remote port.
     * @return remote port for the given connection.
     */
    default int remotePort() {
        return 0;
    }

}
