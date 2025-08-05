/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.internal;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;

class SpanNameUtilTests {

    @Test
    void should_convert_a_name_in_hyphen_based_notation() throws Exception {
        BDDAssertions.then(SpanNameUtil.toLowerHyphen("aMethodNameInCamelCaseNotation"))
            .isEqualTo("a-method-name-in-camel-case-notation");
    }

    @Test
    void should_convert_a_class_name_in_hyphen_based_notation() throws Exception {
        BDDAssertions.then(SpanNameUtil.toLowerHyphen("MySuperClassName")).isEqualTo("my-super-class-name");
    }

    @Test
    void should_not_shorten_a_name_that_is_below_max_threshold() throws Exception {
        BDDAssertions.then(SpanNameUtil.shorten("someName")).isEqualTo("someName");
    }

    @Test
    void should_not_shorten_a_name_that_is_null() throws Exception {
        BDDAssertions.then(SpanNameUtil.shorten(null)).isNull();
    }

    @Test
    void should_shorten_a_name_that_is_above_max_threshold() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            sb.append("a");
        }
        BDDAssertions.then(SpanNameUtil.shorten(sb.toString()).length()).isEqualTo(SpanNameUtil.MAX_NAME_LENGTH);
    }

}
