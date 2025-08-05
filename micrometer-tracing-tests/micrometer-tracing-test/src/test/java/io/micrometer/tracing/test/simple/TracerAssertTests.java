/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import org.junit.jupiter.api.Test;

import static io.micrometer.tracing.test.simple.TracerAssert.assertThat;
import static org.assertj.core.api.BDDAssertions.thenNoException;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

class TracerAssertTests {

    @Test
    void should_not_throw_exception_when_name_correct() {
        SimpleTracer simpleTracer = new SimpleTracer();

        simpleTracer.nextSpan().name("foo").start().end();

        thenNoException().isThrownBy(() -> assertThat(simpleTracer).onlySpan().hasNameEqualTo("foo"));
    }

    @Test
    void should_throw_exception_when_name_incorrect() {
        SimpleTracer simpleTracer = new SimpleTracer();

        simpleTracer.nextSpan().name("foo").start().end();

        thenThrownBy(() -> assertThat(simpleTracer).lastSpan().hasNameEqualTo("bar"))
            .isInstanceOf(AssertionError.class);
    }

    @Test
    void should_not_throw_exception_when_name_correct_using_last_span() {
        SimpleTracer simpleTracer = new SimpleTracer();

        simpleTracer.nextSpan().name("foo").start().end();

        thenNoException().isThrownBy(() -> assertThat(simpleTracer).onlySpan().hasNameEqualTo("foo"));
    }

    @Test
    void should_throw_exception_when_name_incorrect_using_last_span() {
        SimpleTracer simpleTracer = new SimpleTracer();

        simpleTracer.nextSpan().name("foo").start().end();

        thenThrownBy(() -> assertThat(simpleTracer).lastSpan().hasNameEqualTo("bar"))
            .isInstanceOf(AssertionError.class);
    }

    @Test
    void should_not_throw_exception_when_size_of_reported_spans_is_correct() {
        SimpleTracer simpleTracer = new SimpleTracer();

        simpleTracer.nextSpan().name("foo").start().end();

        thenNoException().isThrownBy(() -> assertThat(simpleTracer).reportedSpans().hasSize(1));
    }

    @Test
    void should_throw_exception_when_size_reported_spans_is_invalid() {
        SimpleTracer simpleTracer = new SimpleTracer();

        simpleTracer.nextSpan().name("foo").start().end();

        thenThrownBy(() -> assertThat(simpleTracer).reportedSpans().hasSize(2)).isInstanceOf(AssertionError.class);
    }

    @Test
    void should_satisfy_reported_spans() {
        SimpleTracer simpleTracer = new SimpleTracer();

        simpleTracer.nextSpan().name("foo").start().end();

        assertThat(simpleTracer).reportedSpans()
            .hasSize(1)
            .satisfies(simpleSpans -> SpansAssert.assertThat(simpleSpans).hasASpanWithName("foo"));
    }

}
