/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test;

class SampleTestRunnerTraceSetupTests extends SampleTestRunner {

    SampleTestRunnerTraceSetupTests() {
        super(SampleRunnerConfig.builder().wavefrontUrl("foo").wavefrontToken("bar").build());
    }

    @Override
    public TracingSetup[] getTracingSetup() {
        return new TracingSetup[] {};
    }

    @Override
    public SampleTestRunnerConsumer yourCode() {
        throw new AssertionError("This code should not be ran!");
    }

}
