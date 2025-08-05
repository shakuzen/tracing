/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.internal;

import io.micrometer.tracing.SpanName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class DefaultSpanNamerTests {

    @Test
    void should_override_default_name_with_one_from_annotation() {
        then(new DefaultSpanNamer().name(new WithAnnotation(), "default")).isEqualTo("foo");
    }

    @Test
    void should_override_default_name_with_one_from_overridden_to_string() {
        then(new DefaultSpanNamer().name(new WithToString(), "default")).isEqualTo("foo");
    }

    @Test
    void should_override_default_name_with_default_when_default_to_string() {
        then(new DefaultSpanNamer().name(new WithDefaultToString(), "foo")).isEqualTo("foo");
    }

    @SpanName("foo")
    static class WithAnnotation {

    }

    static class WithToString {

        @Override
        public String toString() {
            return "foo";
        }

    }

    static class WithDefaultToString {

    }

}
