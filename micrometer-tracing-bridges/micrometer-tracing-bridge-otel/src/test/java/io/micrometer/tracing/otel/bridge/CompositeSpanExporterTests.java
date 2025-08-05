/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.tracing.exporter.SpanExportingPredicate;
import io.micrometer.tracing.exporter.SpanFilter;
import io.micrometer.tracing.exporter.SpanReporter;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CompositeSpanExporterTests {

    @Test
    void should_call_predicate_filter_reporter_and_then_exporter() {
        SpanExporter exporter = mock(SpanExporter.class);
        given(exporter.export(BDDMockito.any())).willReturn(CompletableResultCode.ofSuccess());
        SpanExportingPredicate predicate = span -> span.getName().equals("foo");
        SpanFilter filter = span -> span.setName("baz");
        SpanReporter reporter = mock(SpanReporter.class);

        SpanData fooSpan = new CustomSpanData("foo");
        SpanData barSpan = new CustomSpanData("bar");

        CompletableResultCode resultCode = new CompositeSpanExporter(Collections.singleton(exporter),
                Collections.singletonList(predicate), Collections.singletonList(reporter),
                Collections.singletonList(filter))
            .export(Arrays.asList(fooSpan, barSpan));

        then(reporter).should().report(BDDMockito.argThat(finishedSpan -> "baz".equals(finishedSpan.getName())));
        then(exporter).should()
            .export(BDDMockito.argThat(spans -> spans.size() == 1 && "baz".equals(spans.iterator().next().getName())));
        BDDAssertions.then(resultCode.isSuccess()).isTrue();
    }

    @Test
    void should_flush_all_exporters() {
        SpanExporter exporter = mock(SpanExporter.class);
        given(exporter.flush()).willReturn(CompletableResultCode.ofSuccess());

        CompletableResultCode resultCode = new CompositeSpanExporter(Collections.singleton(exporter), null, null, null)
            .flush();

        then(exporter).should().flush();
        BDDAssertions.then(resultCode.isSuccess()).isTrue();
    }

    @Test
    void should_shutdown_all_exporters() {
        SpanExporter exporter = mock(SpanExporter.class);
        given(exporter.shutdown()).willReturn(CompletableResultCode.ofSuccess());

        CompletableResultCode resultCode = new CompositeSpanExporter(Collections.singleton(exporter), null, null, null)
            .shutdown();

        verify(exporter).shutdown();
        BDDAssertions.then(resultCode.isSuccess()).isTrue();
    }

    @Test
    void should_not_call_exporter_if_no_spans() {
        SpanExporter exporter = mock(SpanExporter.class);
        given(exporter.export(BDDMockito.any())).willReturn(CompletableResultCode.ofSuccess());
        SpanExportingPredicate predicate = span -> span.getName().equals("foo");
        SpanFilter filter = span -> span.setName("baz");
        SpanReporter reporter = mock(SpanReporter.class);

        SpanData barSpan = new CustomSpanData("bar");

        CompletableResultCode resultCode = new CompositeSpanExporter(Collections.singleton(exporter),
                Collections.singletonList(predicate), Collections.singletonList(reporter),
                Collections.singletonList(filter))
            .export(Collections.singletonList(barSpan));

        then(reporter).shouldHaveNoInteractions();

        then(exporter).shouldHaveNoInteractions();
        BDDAssertions.then(resultCode.isSuccess()).isTrue();
    }

    static class CustomSpanData implements SpanData {

        private String name;

        CustomSpanData(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public SpanKind getKind() {
            return SpanKind.PRODUCER;
        }

        @Override
        public SpanContext getSpanContext() {
            return SpanContext.getInvalid();
        }

        @Override
        public SpanContext getParentSpanContext() {
            return SpanContext.getInvalid();
        }

        @Override
        public StatusData getStatus() {
            return StatusData.ok();
        }

        @Override
        public long getStartEpochNanos() {
            return 0L;
        }

        @Override
        public Attributes getAttributes() {
            return Attributes.empty();
        }

        @Override
        public List<EventData> getEvents() {
            return Collections.emptyList();
        }

        @Override
        public List<LinkData> getLinks() {
            return Collections.emptyList();
        }

        @Override
        public long getEndEpochNanos() {
            return 0L;
        }

        @Override
        public boolean hasEnded() {
            return true;
        }

        @Override
        public int getTotalRecordedEvents() {
            return 10;
        }

        @Override
        public int getTotalRecordedLinks() {
            return 20;
        }

        @Override
        public int getTotalAttributeCount() {
            return 30;
        }

        @Override
        public InstrumentationLibraryInfo getInstrumentationLibraryInfo() {
            return InstrumentationLibraryInfo.empty();
        }

        @Override
        public InstrumentationScopeInfo getInstrumentationScopeInfo() {
            return InstrumentationScopeInfo.empty();
        }

        @Override
        public Resource getResource() {
            return Resource.empty();
        }

    }

}
