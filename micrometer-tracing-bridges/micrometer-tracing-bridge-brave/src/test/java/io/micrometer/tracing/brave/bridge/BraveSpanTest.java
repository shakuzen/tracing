/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import brave.Tracing;
import brave.test.TestSpanHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BraveSpanTest {

    TestSpanHandler handler = new TestSpanHandler();

    Tracing tracing = Tracing.newBuilder().addSpanHandler(handler).build();

    @AfterEach
    void cleanup() {
        tracing.close();
    }

    @Test
    void should_set_non_string_tags() {
        new BraveSpan(tracing.tracer().nextSpan()).start()
            .tag("string", "string")
            .tag("double", 2.5)
            .tag("long", 2)
            .tag("boolean", true)
            .end();

        assertThat(handler.get(0).tags()).containsEntry("string", "string")
            .containsEntry("double", "2.5")
            .containsEntry("long", "2")
            .containsEntry("boolean", "true");
    }

}
