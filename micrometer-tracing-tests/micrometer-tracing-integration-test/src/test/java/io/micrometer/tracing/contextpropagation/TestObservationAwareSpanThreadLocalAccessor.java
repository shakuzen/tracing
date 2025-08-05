/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.contextpropagation;

import io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor.SpanAction;
import java.util.Map;

public class TestObservationAwareSpanThreadLocalAccessor {

    public static Map<Thread, SpanAction> spanActions(ObservationAwareSpanThreadLocalAccessor accessor) {
        return accessor.spanActions;
    }

}
