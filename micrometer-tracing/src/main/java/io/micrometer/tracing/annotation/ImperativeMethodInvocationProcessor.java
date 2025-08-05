/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.common.annotation.ValueExpressionResolver;
import io.micrometer.common.annotation.ValueResolver;
import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.aopalliance.intercept.MethodInvocation;

import java.util.function.Function;

/**
 * Method Invocation processor for imperative code. Code ported from Spring Cloud Sleuth.
 *
 * @author Marcin Grzejszczak
 * @since 1.1.0
 */
public class ImperativeMethodInvocationProcessor extends AbstractMethodInvocationProcessor {

    /**
     * Creates a new instance of {@link ImperativeMethodInvocationProcessor}.
     * @param newSpanParser new span parser
     * @param tracer tracer
     * @param resolverProvider converts a class into an instance of resolver provider
     * @param expressionResolverProvider converts a class into an instance of expression
     * resolver provider
     */
    public ImperativeMethodInvocationProcessor(NewSpanParser newSpanParser, Tracer tracer,
            Function<Class<? extends ValueResolver>, ? extends ValueResolver> resolverProvider,
            Function<Class<? extends ValueExpressionResolver>, ? extends ValueExpressionResolver> expressionResolverProvider) {
        this(newSpanParser, tracer, new SpanTagAnnotationHandler(resolverProvider, expressionResolverProvider));
    }

    /**
     * Creates a new instance of {@link ImperativeMethodInvocationProcessor}.
     * @param newSpanParser new span parser
     * @param tracer tracer
     */
    public ImperativeMethodInvocationProcessor(NewSpanParser newSpanParser, Tracer tracer) {
        this(newSpanParser, tracer, null);
    }

    /**
     * Creates a new instance of {@link ImperativeMethodInvocationProcessor}.
     * @param newSpanParser new span parser
     * @param tracer tracer
     * @param spanTagAnnotationHandler resolves tags to be added to the span from the
     * annotations
     */
    public ImperativeMethodInvocationProcessor(NewSpanParser newSpanParser, Tracer tracer,
            @Nullable SpanTagAnnotationHandler spanTagAnnotationHandler) {
        super(newSpanParser, tracer, tracer.currentTraceContext(), spanTagAnnotationHandler);
    }

    @Override
    public Object process(MethodInvocation invocation, NewSpan newSpan, ContinueSpan continueSpan) throws Throwable {
        return proceedUnderSynchronousSpan(invocation, newSpan, continueSpan);
    }

    private Object proceedUnderSynchronousSpan(MethodInvocation invocation, NewSpan newSpan, ContinueSpan continueSpan)
            throws Throwable {
        Span span = tracer.currentSpan();
        // in case of @ContinueSpan and no span in tracer we start new span and should
        // close it on completion
        boolean startNewSpan = newSpan != null || span == null;
        if (startNewSpan) {
            span = tracer.nextSpan();
            newSpanParser.parse(invocation, newSpan, span);
            span.start();
        }
        String log = log(continueSpan);
        boolean hasLog = StringUtils.isNotBlank(log);
        try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
            before(invocation, span, log, hasLog);
            return invocation.proceed();
        }
        catch (Exception ex) {
            onFailure(span, log, hasLog, ex);
            throw ex;
        }
        finally {
            after(span, startNewSpan, log, hasLog);
        }
    }

}
