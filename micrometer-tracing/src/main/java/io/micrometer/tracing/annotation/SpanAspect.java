/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * <p>
 * AspectJ aspect for intercepting types or methods annotated with
 * {@link NewSpan @NewSpan} or {@link ContinueSpan @ContinueSpan}.
 *
 * @author Marcin Grzejszczak
 * @since 1.1.0
 * @see ImperativeMethodInvocationProcessor
 */
@Aspect
@NonNullApi
public class SpanAspect {

    private final MethodInvocationProcessor methodInvocationProcessor;

    public SpanAspect(MethodInvocationProcessor methodInvocationProcessor) {
        this.methodInvocationProcessor = methodInvocationProcessor;
    }

    @Around("@annotation(io.micrometer.tracing.annotation.ContinueSpan)")
    @Nullable
    public Object continueSpanMethod(ProceedingJoinPoint pjp) throws Throwable {
        Method method = getMethod(pjp);
        ContinueSpan continueSpan = method.getAnnotation(ContinueSpan.class);
        return methodInvocationProcessor.process(new SpanAspectMethodInvocation(pjp, method), null, continueSpan);
    }

    @Around("@annotation(io.micrometer.tracing.annotation.NewSpan)")
    @Nullable
    public Object newSpanMethod(ProceedingJoinPoint pjp) throws Throwable {
        Method method = getMethod(pjp);
        NewSpan newSpan = method.getAnnotation(NewSpan.class);
        return methodInvocationProcessor.process(new SpanAspectMethodInvocation(pjp, method), newSpan, null);
    }

    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        return pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
    }

}
