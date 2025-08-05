/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;

/**
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
final class DeprecatedClassLogger {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(DeprecatedClassLogger.class);

    static void logWarning(Class<?> clazz) {
        log.warn("The class <{}> is scheduled for removal in the next minor. Please migrate away from using it.",
                clazz);
    }

}
