/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.observation.transport.Kind;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * Abstract request type used for parsing and sampling. Represents an HTTP Client request.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.10.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface HttpClientRequest extends HttpRequest {

    /**
     * Adds a new header.
     * @param name header name
     * @param value header value
     */
    void header(String name, String value);

    @Override
    default Kind kind() {
        return Kind.CLIENT;
    }

}
