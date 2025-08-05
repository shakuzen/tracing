/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import java.util.Collections;
import java.util.List;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.exporter.SpanExportingPredicate;
import io.micrometer.tracing.exporter.SpanFilter;
import io.micrometer.tracing.exporter.SpanReporter;

/**
 * Wraps the {@link SpanHandler} with additional predicate, reporting and filtering logic.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class CompositeSpanHandler extends SpanHandler {

    private final List<SpanExportingPredicate> filters;

    private final List<SpanReporter> reporters;

    private final List<SpanFilter> spanFilters;

    /**
     * Creates a new instance of {@link CompositeSpanHandler}.
     * @param predicates predicates that decide which spans should be exported
     * @param reporters reporters that export spans
     * @param spanFilters filters that mutate spans before reporting them
     */
    public CompositeSpanHandler(List<SpanExportingPredicate> predicates, List<SpanReporter> reporters,
            List<SpanFilter> spanFilters) {
        this.filters = predicates == null ? Collections.emptyList() : predicates;
        this.reporters = reporters == null ? Collections.emptyList() : reporters;
        this.spanFilters = spanFilters == null ? Collections.emptyList() : spanFilters;
    }

    @Override
    public boolean end(TraceContext context, MutableSpan span, Cause cause) {
        if (cause != Cause.FINISHED) {
            return true;
        }
        boolean shouldProcess = shouldProcess(span);
        if (!shouldProcess) {
            return false;
        }
        shouldProcess = super.end(context, span, cause);
        if (!shouldProcess) {
            return false;
        }
        FinishedSpan modified = BraveFinishedSpan.fromBrave(span);
        for (SpanFilter spanFilter : this.spanFilters) {
            modified = spanFilter.map(modified);
        }
        for (SpanReporter reporter : this.reporters) {
            reporter.report(modified);
        }
        return true;
    }

    private boolean shouldProcess(MutableSpan span) {
        for (SpanExportingPredicate exporter : this.filters) {
            if (!exporter.isExportable(BraveFinishedSpan.fromBrave(span))) {
                return false;
            }
        }
        return true;
    }

}
