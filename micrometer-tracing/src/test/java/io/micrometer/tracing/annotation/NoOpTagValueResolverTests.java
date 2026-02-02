/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.common.annotation.NoOpValueResolver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

/**
 * @author Marcin Grzejszczak
 */
class NoOpValueResolverTests {

    @Test
    void should_return_null() throws Exception {
        then(new NoOpValueResolver().resolve("")).isNull();
    }

}
