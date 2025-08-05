/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.observation.transport.Kind;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * Abstract request type used for parsing and sampling. Represents an HTTP Server request.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.10.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface HttpServerRequest extends HttpRequest {

    /**
     * Returns an HTTP attribute.
     * @param key attribute key
     * @return attribute with the given key or {@code null} if not set
     */
    default Object getAttribute(String key) {
        return null;
    }

    /**
     * Sets an HTTP attribute.
     * @param key attribute key
     * @param value attribute value
     */
    default void setAttribute(String key, Object value) {

    }

    @Override
    default Kind kind() {
        return Kind.SERVER;
    }

}
