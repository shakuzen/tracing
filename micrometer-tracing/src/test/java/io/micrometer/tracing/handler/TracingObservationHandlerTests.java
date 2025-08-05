/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.handler;

import io.micrometer.observation.Observation;
import io.micrometer.observation.Observation.Event;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InOrder;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenNoException;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class TracingObservationHandlerTests {

    Tracer tracer = mock(Tracer.class);

    CurrentTraceContext currentTraceContext = mock(CurrentTraceContext.class);

    @BeforeEach
    void setup() {
        given(tracer.currentTraceContext()).willReturn(currentTraceContext);
    }

    @Test
    void iseShouldBeCalledWhenNoSpanInContext() {
        TracingObservationHandler<Observation.Context> handler = () -> null;

        thenThrownBy(() -> handler.onEvent(() -> "", new Observation.Context()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Span wasn't started");
    }

    @Test
    void spanShouldBeClearedOnScopeReset() {
        TracingObservationHandler<Observation.Context> handler = () -> tracer;
        Observation.Context context = new Observation.Context();
        TracingObservationHandler.TracingContext tracingContext = new TracingObservationHandler.TracingContext();
        CurrentTraceContext.Scope scope1 = mock(CurrentTraceContext.Scope.class);
        CurrentTraceContext.Scope scope2 = mock(CurrentTraceContext.Scope.class);
        RevertingScope revertingScope1 = new RevertingScope(tracingContext, scope1, null);
        RevertingScope revertingScope2 = new RevertingScope(tracingContext, scope2, revertingScope1);
        tracingContext.setScope(revertingScope2);
        context.put(TracingObservationHandler.TracingContext.class, tracingContext);

        handler.onScopeReset(context);

        InOrder inOrder = inOrder(scope2, scope1);
        inOrder.verify(scope2).close();
        inOrder.verify(scope1).close();
        BDDMockito.then(currentTraceContext).should().maybeScope(null);
    }

    @Test
    void nullScopeShouldBeSupported() {
        TracingObservationHandler<Observation.Context> handler = () -> null;

        thenNoException().isThrownBy(() -> handler.onScopeClosed(new Observation.Context()));
    }

    @Test
    void spanShouldNotBeOverriddenWhenResettingScope() {
        Span span = mock(Span.class);
        Observation.Context context = new Observation.Context();
        TracingObservationHandler.TracingContext tracingContext = new TracingObservationHandler.TracingContext();
        tracingContext.setSpan(span);
        context.put(TracingObservationHandler.TracingContext.class, tracingContext);
        TracingObservationHandler<Observation.Context> handler = () -> tracer;

        handler.onScopeReset(context);

        assertThat(tracingContext.getSpan()).isSameAs(span);
    }

    @Test
    void eventTimestamp() {
        Span span = mock(Span.class);
        Observation.Context context = new Observation.Context();
        TracingObservationHandler.TracingContext tracingContext = new TracingObservationHandler.TracingContext();
        tracingContext.setSpan(span);
        context.put(TracingObservationHandler.TracingContext.class, tracingContext);
        TracingObservationHandler<Observation.Context> handler = () -> tracer;

        Event defaultEvent = () -> "default-event";
        handler.onEvent(defaultEvent, context);
        BDDMockito.then(span).should().event("default-event");

        BDDMockito.reset(span);

        Event eventWithWallTime = new Event() {
            @Override
            public String getName() {
                return "walltime-event";
            }

            @Override
            public long getWallTime() {
                return 100;
            }
        };
        handler.onEvent(eventWithWallTime, context);
        BDDMockito.then(span).should().event("walltime-event", 100, TimeUnit.MILLISECONDS);
    }

}
