/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.internal;

import java.lang.reflect.Method;

import io.micrometer.tracing.SpanName;
import io.micrometer.tracing.SpanNamer;

/**
 * Default implementation of SpanNamer that tries to get the span name as follows:
 * <p>
 * * from the @SpanName annotation on the class if one is present.
 * <p>
 * * from the @SpanName annotation on the method if passed object is of a {@link Method}.
 * type
 * <p>
 * * from the toString() of the delegate if it's not the default
 * {@link Object#toString()}.
 * <p>
 * * the default provided value.
 *
 * @author Marcin Grzejszczak
 * @see SpanName
 * @since 1.0.0
 */
public class DefaultSpanNamer implements SpanNamer {

    private static boolean isDefaultToString(Object delegate, String spanName) {
        if (delegate instanceof Method) {
            return delegate.toString().equals(spanName);
        }
        return (delegate.getClass().getName() + "@" + Integer.toHexString(delegate.hashCode())).equals(spanName);
    }

    @Override
    public String name(Object object, String defaultValue) {
        SpanName annotation = annotation(object);
        String spanName = annotation != null ? annotation.value() : object.toString();
        // If there is no overridden toString method we'll put a constant value
        if (isDefaultToString(object, spanName)) {
            return defaultValue;
        }
        return spanName;
    }

    private SpanName annotation(Object o) {
        if (o instanceof Method) {
            return ((Method) o).getAnnotation(SpanName.class);
        }
        return o.getClass().getAnnotation(SpanName.class);
    }

}
