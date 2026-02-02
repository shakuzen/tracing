/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.contextpropagation;

public class TestObservationAwareBaggageThreadLocalAccessor {

    public static void closeCurrentScope(ObservationAwareBaggageThreadLocalAccessor accessor) {
        accessor.closeCurrentScope();
    }

}
