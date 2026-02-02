/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.common.util.StringUtils;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.internal.SpanNameUtil;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Default implementation of the {@link NewSpanParser} that parses only the span name.
 *
 * Code ported from Spring Cloud Sleuth.
 *
 * @author Christian Schwerdtfeger
 * @since 1.1.0
 */
public class DefaultNewSpanParser implements NewSpanParser {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(DefaultNewSpanParser.class);

    @Override
    public void parse(MethodInvocation pjp, NewSpan newSpan, Span span) {
        String name = spanName(newSpan, pjp);
        String changedName = SpanNameUtil.toLowerHyphen(name);
        if (log.isDebugEnabled()) {
            log.debug("For the class [" + pjp.getThis().getClass() + "] method " + "[" + pjp.getMethod().getName()
                    + "] will name the span [" + changedName + "]");
        }
        span.name(changedName);
    }

    private String spanName(NewSpan newSpan, MethodInvocation pjp) {
        if (newSpan == null) {
            return pjp.getMethod().getName();
        }
        String name = newSpan.name();
        String value = newSpan.value();
        boolean nameEmpty = StringUtils.isEmpty(name);
        if (nameEmpty && StringUtils.isEmpty(value)) {
            return pjp.getMethod().getName();
        }
        return nameEmpty ? value : name;
    }

}
