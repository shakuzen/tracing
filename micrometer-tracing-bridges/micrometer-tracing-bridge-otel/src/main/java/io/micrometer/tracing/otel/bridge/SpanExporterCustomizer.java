/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.opentelemetry.sdk.trace.export.SpanExporter;

/**
 * Allows customization of a {@link SpanExporter}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface SpanExporterCustomizer {

    /**
     * Customizes a span exporter.
     * @param spanExporter to customize
     * @return customized span exporter
     */
    default SpanExporter customize(SpanExporter spanExporter) {
        return spanExporter;
    }

}
