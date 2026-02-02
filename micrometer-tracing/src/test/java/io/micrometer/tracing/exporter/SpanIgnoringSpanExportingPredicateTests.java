/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.exporter;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import static org.assertj.core.api.BDDAssertions.then;

class SpanIgnoringSpanExportingPredicateTests {

    private FinishedSpan namedSpan() {
        FinishedSpan span = BDDMockito.mock(FinishedSpan.class);
        BDDMockito.given(span.getName()).willReturn("someName");
        return span;
    }

    @Test
    void should_not_handle_span_when_present_in_main_list_of_spans_to_skip() {
        SpanIgnoringSpanExportingPredicate handler = new SpanIgnoringSpanExportingPredicate(
                Collections.singletonList("someName"), Collections.emptyList());

        then(handler.isExportable(namedSpan())).isFalse();
    }

    @Test
    void should_not_handle_span_when_present_in_additional_list_of_spans_to_skip() {
        SpanIgnoringSpanExportingPredicate handler = new SpanIgnoringSpanExportingPredicate(Collections.emptyList(),
                Collections.singletonList("someName"));

        then(handler.isExportable(namedSpan())).isFalse();
    }

    @Test
    void should_use_cached_entry_for_same_patterns() {
        export(handler("someOtherName"));
        export(handler("someOtherName"));
        export(handler("someOtherName"));

        then(SpanIgnoringSpanExportingPredicate.cache).containsKey("someOtherName");

        export(handler("a"));
        export(handler("b"));
        export(handler("c"));

        then(SpanIgnoringSpanExportingPredicate.cache).containsKey("someOtherName")
            .containsKey("a")
            .containsKey("b")
            .containsKey("c");
    }

    private void export(SpanIgnoringSpanExportingPredicate handler) {
        handler.isExportable(namedSpan());
    }

    private SpanIgnoringSpanExportingPredicate handler(String name) {
        return new SpanIgnoringSpanExportingPredicate(Collections.emptyList(), Collections.singletonList(name));
    }

}
