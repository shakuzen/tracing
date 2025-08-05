/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.contextpropagation;

import java.util.Map;

public class TestBaggageThreadLocalAccessor {

    public static Map<Thread, ? extends Object> baggageInScope(ObservationAwareBaggageThreadLocalAccessor accessor) {
        return accessor.baggageInScope;
    }

}
