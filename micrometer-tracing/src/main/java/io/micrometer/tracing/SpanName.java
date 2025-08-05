/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide the name for the span. You should annotate all your custom
 * {@link Runnable Runnable} or {@link java.util.concurrent.Callable Callable} classes for
 * the instrumentation logic to pick up how to name the span.
 *
 * Having for example the following code <pre>{@code
 *     &#64;SpanName("custom-operation")
 *     class CustomRunnable implements Runnable {
 *         &#64;Override
 *         public void run() {
 *          // latency of this method will be recorded in a span named "custom-operation"
 *         }
 *      }
 * }</pre>
 *
 * Will result in creating a span with name {@code custom-operation}.
 *
 * When there's no @SpanName annotation, {@code toString} is used. Here's an example of
 * the above, but via an anonymous instance. <pre>{@code
 *     return new Runnable() {
 *          -- snip --
 *
 *          &#64;Override
 *          public String toString() {
 *              return "custom-operation";
 *          }
 *     };
 * }</pre>
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpanName {

    /**
     * Name of the span to be resolved at runtime.
     * @return - value of the span name.
     */
    String value();

}
