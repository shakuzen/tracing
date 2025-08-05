/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.propagation;

import brave.propagation.Propagation;

/**
 * Provides logic for supplying of a {@link Propagation.Factory}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface PropagationFactorySupplier {

    /**
     * Creates an instance of a {@link Propagation.Factory}.
     * @return an instance of a {@link Propagation.Factory}
     */
    Propagation.Factory get();

}
