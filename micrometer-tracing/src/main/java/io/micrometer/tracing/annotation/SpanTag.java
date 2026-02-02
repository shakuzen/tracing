/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.common.annotation.NoOpValueResolver;
import io.micrometer.common.annotation.ValueResolver;

import java.lang.annotation.*;

/**
 * There are 3 different ways to add tags to a span. All of them are controlled by the
 * annotation values. Precedence is:
 * <p>
 * try with the {@link ValueResolver} bean if the value of the bean wasn't set, try to
 * evaluate a SPEL expression if there’s no SPEL expression just return a
 * {@code toString()} value of the parameter
 *
 * @author Christian Schwerdtfeger
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.PARAMETER)
public @interface SpanTag {

    /**
     * The name of the key of the tag which should be created.
     * @return the tag key
     */
    String value() default "";

    /**
     * The name of the key of the tag which should be created.
     * @return the tag value
     */
    String key() default "";

    /**
     * Execute this expression to calculate the tag value. Will be analyzed if no value of
     * the {@link SpanTag#resolver()} was set.
     * @return an expression
     */
    String expression() default "";

    /**
     * Use this bean to resolve the tag value. Has the highest precedence.
     * @return {@link ValueResolver} bean
     */
    Class<? extends ValueResolver> resolver() default NoOpValueResolver.class;

}
