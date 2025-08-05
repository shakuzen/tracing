/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.exporter;

/**
 * An interface that allows to mutate a finished Span.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface SpanFilter {

    /**
     * Called to mutate a {@link FinishedSpan} before its filtering.
     * @param span a finished span to mutate
     * @return mutated span
     */
    FinishedSpan map(FinishedSpan span);

}
