/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.common.annotation.ValueExpressionResolver;
import io.micrometer.common.annotation.ValueResolver;
import io.micrometer.tracing.SpanCustomizer;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class NullSpanTagAnnotationHandlerTests {

    ValueResolver ValueResolver = parameter -> null;

    ValueExpressionResolver ValueExpressionResolver = (expression, parameter) -> "";

    SpanCustomizer spanCustomizer = new SpanCustomizer() {
        @Override
        public SpanCustomizer name(String name) {
            return this;
        }

        @Override
        public SpanCustomizer tag(String key, String value) {
            return this;
        }

        @Override
        public SpanCustomizer event(String value) {
            return this;
        }
    };

    SpanTagAnnotationHandler handler = new SpanTagAnnotationHandler(aClass -> ValueResolver,
            aClass -> ValueExpressionResolver);

    @Test
    void shouldUseEmptyStringWheCustomValueResolverReturnsNull() throws NoSuchMethodException, SecurityException {
        Method method = AnnotationMockClass.class.getMethod("getAnnotationForValueResolver", String.class);
        Annotation annotation = method.getParameterAnnotations()[0][0];
        if (annotation instanceof SpanTag) {
            String resolvedValue = this.handler.resolveTagValue((SpanTag) annotation, "test", aClass -> ValueResolver,
                    aClass -> ValueExpressionResolver);
            assertThat(resolvedValue).isEqualTo("");
        }
        else {
            fail("Annotation was not SpanTag");
        }
    }

    @Test
    void shouldUseEmptyStringWhenTagValueExpressionReturnNull() throws NoSuchMethodException, SecurityException {
        Method method = AnnotationMockClass.class.getMethod("getAnnotationForTagValueExpression", String.class);
        Annotation annotation = method.getParameterAnnotations()[0][0];
        if (annotation instanceof SpanTag) {
            String resolvedValue = this.handler.resolveTagValue((SpanTag) annotation, "test", aClass -> ValueResolver,
                    aClass -> ValueExpressionResolver);

            assertThat(resolvedValue).isEqualTo("");
        }
        else {
            fail("Annotation was not SpanTag");
        }
    }

    @Test
    void shouldUseEmptyStringWhenArgumentIsNull() throws NoSuchMethodException, SecurityException {
        Method method = AnnotationMockClass.class.getMethod("getAnnotationForArgumentToString", Long.class);
        Annotation annotation = method.getParameterAnnotations()[0][0];
        if (annotation instanceof SpanTag) {
            String resolvedValue = this.handler.resolveTagValue((SpanTag) annotation, null, aClass -> ValueResolver,
                    aClass -> ValueExpressionResolver);
            assertThat(resolvedValue).isEqualTo("");
        }
        else {
            fail("Annotation was not SpanTag");
        }
    }

    protected class AnnotationMockClass {

        @NewSpan
        public void getAnnotationForValueResolver(@SpanTag(key = "test", resolver = ValueResolver.class) String test) {
        }

        @NewSpan
        public void getAnnotationForTagValueExpression(@SpanTag(key = "test", expression = "null") String test) {
        }

        @NewSpan
        public void getAnnotationForArgumentToString(@SpanTag("test") Long param) {
        }

    }

}
