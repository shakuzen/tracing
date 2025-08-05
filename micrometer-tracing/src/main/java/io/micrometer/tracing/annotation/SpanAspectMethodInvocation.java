/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.annotation;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

class SpanAspectMethodInvocation implements MethodInvocation {

    private final ProceedingJoinPoint pjp;

    private final Method method;

    SpanAspectMethodInvocation(ProceedingJoinPoint pjp, Method method) {
        this.pjp = pjp;
        this.method = method;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return pjp.getArgs();
    }

    @Override
    public Object proceed() throws Throwable {
        return pjp.proceed();
    }

    @Override
    public Object getThis() {
        return pjp.getThis();
    }

    @Override
    public AccessibleObject getStaticPart() {
        return getMethod();
    }

    ProceedingJoinPoint getPjp() {
        return pjp;
    }

}
