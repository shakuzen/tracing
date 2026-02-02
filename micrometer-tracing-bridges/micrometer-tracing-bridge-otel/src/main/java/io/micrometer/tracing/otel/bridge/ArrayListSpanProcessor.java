/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Stores spans in a queue.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class ArrayListSpanProcessor implements SpanProcessor, SpanExporter {

    private final Queue<SpanData> spans = new ConcurrentLinkedQueue<>();

    @Override
    public void onStart(Context parent, ReadWriteSpan span) {

    }

    @Override
    public boolean isStartRequired() {
        return false;
    }

    @Override
    public void onEnd(ReadableSpan span) {
        this.spans.add(span.toSpanData());
    }

    @Override
    public boolean isEndRequired() {
        return true;
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> spans) {
        this.spans.addAll(spans.stream().filter(f -> !this.spans.contains(f)).collect(Collectors.toList()));
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode forceFlush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public void close() {
        shutdown().join(10, SECONDS);
    }

    /**
     * Returns the first collected span.
     * @return the first span
     */
    public SpanData takeLocalSpan() {
        return this.spans.poll();
    }

    /**
     * Returns collected spans.
     * @return collected spans
     */
    public Queue<SpanData> spans() {
        return this.spans;
    }

    /**
     * Clears the stored spans.
     */
    public void clear() {
        this.spans.clear();
    }

    @Override
    public String toString() {
        return "ArrayListSpanProcessor{" + "spans=" + spans + '}';
    }

}
