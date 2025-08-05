/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.annotation;

/**
 * A container class that holds information about the parameter of the annotated method
 * argument.
 *
 * Code ported from Spring Cloud Sleuth.
 *
 * @author Christian Schwerdtfeger
 */
class AnnotatedParameter {

    final int parameterIndex;

    final SpanTag annotation;

    final Object argument;

    AnnotatedParameter(int parameterIndex, SpanTag annotation, Object argument) {
        this.parameterIndex = parameterIndex;
        this.annotation = annotation;
        this.argument = argument;
    }

}
