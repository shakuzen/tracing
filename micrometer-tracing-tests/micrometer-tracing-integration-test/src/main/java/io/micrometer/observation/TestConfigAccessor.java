/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.observation;

import java.util.Collection;

/**
 * Internal, test class - do not use.
 *
 * @since 1.0.0
 */
public final class TestConfigAccessor {

    private TestConfigAccessor() {
        throw new IllegalStateException("Should not instantiate a utility class");
    }

    /**
     * Returns the stored handlers.
     * @param config meter registry config
     * @return stored handlers
     */
    public static Collection<ObservationHandler<?>> getHandlers(ObservationRegistry.ObservationConfig config) {
        return config.getObservationHandlers();
    }

    /**
     * Clears the timer recording handlers.
     * @param config meter registry config
     */
    public static void clearHandlers(ObservationRegistry.ObservationConfig config) {
        config.getObservationHandlers().clear();
    }

}
