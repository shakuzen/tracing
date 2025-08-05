/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

/**
 * Listener to events. Can be used for e.g. reacting to MDC entry changes.
 *
 * @since 1.0.0
 */
public interface EventListener {

    /**
     * Processes an event.
     * @param event event to process
     */
    void onEvent(Object event);

}
