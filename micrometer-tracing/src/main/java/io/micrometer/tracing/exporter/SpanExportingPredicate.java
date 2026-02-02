/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.exporter;

/**
 * An interface that allows to filter whether a given reported span should be exported or
 * not.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface SpanExportingPredicate {

    /**
     * Called to export sampled {@code Span}s.
     * @param span the collection of sampled Spans to be exported.
     * @return whether should export spans
     */
    boolean isExportable(FinishedSpan span);

}
