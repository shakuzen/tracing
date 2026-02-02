/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import java.util.List;
import java.util.Map;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static java.util.stream.Collectors.toMap;

/**
 * A {@link SpanProcessor} that sets baggage entries on a span.
 *
 * @since 1.0.0
 */
public class BaggageTaggingSpanProcessor implements SpanProcessor {

    private final Map<String, AttributeKey<String>> tagsToApply;

    /**
     * Creates a new instance of {@link BaggageTaggingSpanProcessor}.
     * @param tagsToApply tags to apply from the baggage on the span
     */
    public BaggageTaggingSpanProcessor(List<String> tagsToApply) {
        this.tagsToApply = tagsToApply.stream()
            .map(tag -> stringKey(tag))
            .collect(toMap(AttributeKey::getKey, key -> key));
    }

    @Override
    public void onStart(Context context, ReadWriteSpan readWriteSpan) {
        Baggage baggage = Baggage.fromContext(context);

        baggage.forEach((key, baggageEntry) -> {
            AttributeKey<String> attributeKey = tagsToApply.get(key);
            if (attributeKey != null) {
                readWriteSpan.setAttribute(attributeKey, baggageEntry.getValue());
            }
        });
    }

    @Override
    public boolean isStartRequired() {
        return true;
    }

    @Override
    public void onEnd(ReadableSpan readableSpan) {
        // no-op
    }

    @Override
    public boolean isEndRequired() {
        return false;
    }

}
