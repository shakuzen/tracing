/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.http;

import io.micrometer.observation.transport.Kind;

import java.util.Collection;

/**
 * This API is taken from OpenZipkin Brave.
 * <p>
 * Abstract request type used for parsing and sampling.
 *
 * @author OpenZipkin Brave Authors
 * @author Marcin Grzejszczak
 * @since 1.10.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public interface Request {

    /**
     * Returns the header names.
     * @return collection of header names
     */
    Collection<String> headerNames();

    /**
     * Returns the transport kind.
     * @return the remote kind describing the direction and type of the request
     */
    Kind kind();

    /**
     * Returns the underlying request object.
     * @return the underlying request object or {@code null} if there is none
     */
    Object unwrap();

}
