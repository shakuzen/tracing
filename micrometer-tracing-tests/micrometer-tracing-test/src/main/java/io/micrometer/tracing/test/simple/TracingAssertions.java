/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import java.util.Collection;

import io.micrometer.tracing.exporter.FinishedSpan;

/**
 * Entry point assertions.
 *
 * @author Tadaya Tsuyukubo
 * @since 1.0.0
 */
public class TracingAssertions {

    /**
     * Creates a new instance of {@link SpanAssert}.
     * @param actual a span to assert against
     * @return span assertions
     */
    @SuppressWarnings("rawtypes")
    public static SpanAssert assertThat(FinishedSpan actual) {
        return new SpanAssert(actual);
    }

    /**
     * Creates a new instance of {@link SpanAssert}.
     * @param actual a span to assert against
     * @return span assertions
     */
    @SuppressWarnings("rawtypes")
    public static SpanAssert then(FinishedSpan actual) {
        return new SpanAssert(actual);
    }

    /**
     * Creates a new instance of {@link SpansAssert}.
     * @param actual spans to assert against
     * @return spans assertions
     */
    public static SpansAssert assertThat(Collection<FinishedSpan> actual) {
        return new SpansAssert(actual);
    }

    /**
     * Creates a new instance of {@link SpansAssert}.
     * @param actual spans to assert against
     * @return spans assertions
     */
    public static SpansAssert then(Collection<FinishedSpan> actual) {
        return new SpansAssert(actual);
    }

    /**
     * Creates a new instance of {@link TracerAssert}.
     * @param actual a tracer to assert against
     * @return tracer assertions
     */
    public static TracerAssert assertThat(SimpleTracer actual) {
        return new TracerAssert(actual);
    }

    /**
     * Creates a new instance of {@link TracerAssert}.
     * @param actual a tracer to assert against
     * @return tracer assertions
     */
    public static TracerAssert then(SimpleTracer actual) {
        return new TracerAssert(actual);
    }

}
