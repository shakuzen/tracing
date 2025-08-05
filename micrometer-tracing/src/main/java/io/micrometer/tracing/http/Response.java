/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.transport.Kind;

import java.util.Collection;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * Abstract response type used for parsing and sampling.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.10.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface Response {

    /**
     * Returns the header names.
     * @return collection of header names
     */
    Collection<String> headerNames();

    /**
     * Returns the HTTP request.
     * @return corresponding request
     */
    @Nullable
    Request request();

    /**
     * Returns the exception.
     * @return exception that occurred or {@code null} if there was none.
     */
    @Nullable
    Throwable error();

    /**
     * Returns the underlying response object.
     * @return the underlying response object or {@code null} if there is none.
     */
    Object unwrap();

    /**
     * Returns the transport kind.
     * @return the remote kind describing the direction and type of the response
     */
    Kind kind();

}
