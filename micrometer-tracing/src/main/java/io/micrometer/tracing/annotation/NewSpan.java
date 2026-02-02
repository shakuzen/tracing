/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows to create a new span around a public method. The new span will be either a child
 * of an existing span if a trace is already in progress or a new span will be created if
 * there was no previous trace.
 * <p>
 * Method parameters can be annotated with {@link SpanTag}, which will end in adding the
 * parameter value as a tag value to the span. The tag key will be the value of the
 * {@code key} annotation from {@link SpanTag}.
 *
 * @author Christian Schwerdtfeger
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.METHOD)
public @interface NewSpan {

    /**
     * The name of the span which will be created. Default is the annotated method's name
     * separated by hyphens.
     * @return the name of the span which will be created
     */
    String name() default "";

    /**
     * The name of the span which will be created. Default is the annotated method's name
     * separated by hyphens.
     * @return the name of the span which will be created
     */
    String value() default "";

}
