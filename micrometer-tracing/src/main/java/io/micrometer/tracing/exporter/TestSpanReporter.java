/**
 * Copyright © 2024 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2024 the original author or authors.
 */
package io.micrometer.tracing.exporter;

import io.micrometer.common.lang.Nullable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * {@link SpanReporter} with access to stored {@link FinishedSpan}.
 *
 * @author Marcin Grzejszczak
 * @since 1.3.0
 */
public class TestSpanReporter implements SpanReporter {

    private final Queue<FinishedSpan> spans = new ConcurrentLinkedQueue<>();

    /**
     * Polls stored spans for the latest entry.
     * @return latest stored span
     */
    @Nullable
    public FinishedSpan poll() {
        return this.spans.poll();
    }

    /**
     * Returns collected spans.
     * @return collected spans
     */
    public Queue<FinishedSpan> spans() {
        return new ConcurrentLinkedQueue<>(this.spans);
    }

    @Override
    public void report(FinishedSpan span) {
        this.spans.add(span);
    }

    @Override
    public void close() throws Exception {
        this.spans.clear();
    }

}
