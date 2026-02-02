/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

/**
 * Describes how for a given object a span should be named. In the vast majority of cases
 * a name should be provided explicitly. In case of instrumentation where the name has to
 * be resolved at runtime this interface will provide the name of the span.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface SpanNamer {

    /**
     * Retrieves the span name for the given object.
     * @param object - object for which span name should be picked
     * @param defaultValue - the default valued to be returned if span name can't be
     * calculated
     * @return span name
     */
    String name(Object object, String defaultValue);

}
