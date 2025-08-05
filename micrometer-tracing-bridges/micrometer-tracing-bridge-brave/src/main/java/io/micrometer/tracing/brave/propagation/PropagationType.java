/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.propagation;

/**
 * Supported propagation types.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public enum PropagationType {

    /**
     * AWS propagation type.
     */
    AWS,

    /**
     * B3 propagation type.
     */
    B3,

    /**
     * W3C propagation type.
     */
    W3C,

    /**
     * Custom propagation type.
     */
    CUSTOM

}
