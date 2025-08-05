/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.handler;

import io.micrometer.core.instrument.observation.MeterObservationHandler;
import io.micrometer.observation.Observation;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Tests for {@link TracingAwareMeterObservationHandler}.
 *
 * @author Jonatan Ivanov
 */
@ExtendWith(MockitoExtension.class)
class TracingAwareMeterObservationHandlerTests {

    @Mock
    private MeterObservationHandler<Observation.Context> delegate;

    @Mock
    private Tracer tracer;

    @InjectMocks
    private TracingAwareMeterObservationHandler<Observation.Context> handler;

    @Test
    void callsShouldBeDelegated() {
        Observation.Context context = new Observation.Context();
        context.put(TracingObservationHandler.TracingContext.class, new TracingObservationHandler.TracingContext());

        handler.onStart(context);
        verify(delegate).onStart(context);

        handler.onError(context);
        verify(delegate).onError(context);

        Observation.Event event = Observation.Event.of("test");
        handler.onEvent(event, context);
        verify(delegate).onEvent(event, context);

        handler.onScopeOpened(context);
        verify(delegate).onScopeOpened(context);

        handler.onStop(context);
        verify(delegate).onStop(context);

        handler.onScopeClosed(context);
        verify(delegate).onScopeClosed(context);

        handler.supportsContext(context);
        verify(delegate).supportsContext(context);
    }

    @Test
    void spanShouldBeAvailableOnStop() {
        Observation.Context observationContext = new Observation.Context();
        TracingObservationHandler.TracingContext tracingContext = new TracingObservationHandler.TracingContext();
        observationContext.put(TracingObservationHandler.TracingContext.class, tracingContext);

        Span span = mock(Span.class);
        tracingContext.setSpan(span);
        TraceContext traceContext = mock(TraceContext.class);
        CurrentTraceContext currentTraceContext = mock(CurrentTraceContext.class);
        CurrentTraceContext.Scope scope = mock(CurrentTraceContext.Scope.class);

        when(span.context()).thenReturn(traceContext);
        when(tracer.currentTraceContext()).thenReturn(currentTraceContext);
        when(currentTraceContext.maybeScope(traceContext)).thenReturn(scope);

        handler.onStop(observationContext);

        verify(scope).close();
        verify(currentTraceContext).maybeScope(traceContext);
        verify(delegate).onStop(observationContext);
    }

}
