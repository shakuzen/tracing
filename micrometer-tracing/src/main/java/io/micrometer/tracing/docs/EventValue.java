/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.docs;

/**
 * Event value representing a notable event in time.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface EventValue {

    /**
     * @return event value
     */
    String getValue();

}
