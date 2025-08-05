/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.tracing.Span;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Parses data for a span created via a {@link NewSpan} annotation.
 *
 * @author Adrian Cole
 * @since 1.0.0
 */
public interface NewSpanParser {

    /**
     * Override to control the name and tags on an annotation-based span.
     * @param methodInvocation method invocation annotated with new span
     * @param newSpan meta data of the new span
     * @param span span to customize
     */
    void parse(MethodInvocation methodInvocation, NewSpan newSpan, Span span);

}
