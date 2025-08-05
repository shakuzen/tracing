/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.transport.Kind;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * Abstract response type used for parsing and sampling. Represents an HTTP Server
 * response.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.10.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface HttpServerResponse extends HttpResponse {

    @Nullable
    default HttpServerRequest request() {
        return null;
    }

    @Override
    default Throwable error() {
        return null;
    }

    @Override
    default Kind kind() {
        return Kind.SERVER;
    }

}
