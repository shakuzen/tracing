/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.exporter;

/**
 * An interface that allows to process spans after they got finished.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface SpanReporter extends AutoCloseable {

    /**
     * Reports the finished span.
     * @param span a span that was ended and is ready to be reported.
     */
    void report(FinishedSpan span);

    @Override
    default void close() throws Exception {

    }

}
