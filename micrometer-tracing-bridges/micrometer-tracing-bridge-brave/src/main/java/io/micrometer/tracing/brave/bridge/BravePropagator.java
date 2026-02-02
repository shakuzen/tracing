/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import brave.Tracing;
import brave.baggage.BaggageField;
import brave.internal.baggage.BaggageFields;
import brave.propagation.TraceContextOrSamplingFlags;
import io.micrometer.common.lang.Nullable;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.propagation.Propagator;

import java.util.List;

/**
 * Brave implementation of a {@link Propagator}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class BravePropagator implements Propagator {

    private final Tracing tracing;

    /**
     * Creates a new instance of {@link BravePropagator}.
     * @param tracing Brave tracing
     */
    public BravePropagator(Tracing tracing) {
        this.tracing = tracing;
    }

    @Override
    public List<String> fields() {
        return this.tracing.propagation().keys();
    }

    @Override
    public <C> void inject(TraceContext traceContext, @Nullable C carrier, Setter<C> setter) {
        this.tracing.propagation().injector(setter::set).inject(BraveTraceContext.toBrave(traceContext), carrier);
    }

    @Override
    public <C> Span.Builder extract(C carrier, Getter<C> getter) {
        TraceContextOrSamplingFlags extract = this.tracing.propagation().extractor(getter::get).extract(carrier);
        updateExistingBaggageFieldsWithUpdatedValues(extract);
        return BraveSpanBuilder.toBuilder(this.tracing.tracer(), extract);
    }

    private void updateExistingBaggageFieldsWithUpdatedValues(TraceContextOrSamplingFlags extract) {
        if (extract.context() == null) {
            return;
        }
        List<Object> extra = extract.context().extra();
        extra.stream().filter(BraveBaggageFields.class::isInstance).forEach(o -> {
            BraveBaggageFields fields = (BraveBaggageFields) o;
            extra.stream().filter(BaggageFields.class::isInstance).forEach(bf -> {
                BaggageFields baggageFields = (BaggageFields) bf;
                List<BaggageField> allFields = baggageFields.getAllFields();
                fields.getEntries().forEach(e -> {
                    String key = e.getKey().name();
                    String value = e.getValue();
                    allFields.stream()
                        .filter(allField -> allField.name().equals(key))
                        .forEach(allField -> allField.updateValue(extract, value));
                });
            });
        });
    }

}
