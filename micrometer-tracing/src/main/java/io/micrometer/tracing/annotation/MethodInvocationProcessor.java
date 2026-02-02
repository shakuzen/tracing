/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.annotation;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Contract for processing annotations.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface MethodInvocationProcessor {

    /**
     * Executes the annotated method.
     * @param invocation method invocation
     * @param newSpan annotation
     * @param continueSpan annotation
     * @return executed method result
     * @throws Throwable exception upon running a method
     */
    Object process(MethodInvocation invocation, NewSpan newSpan, ContinueSpan continueSpan) throws Throwable;

}
