/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.handler;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.CurrentTraceContext.Scope;
import io.micrometer.tracing.handler.TracingObservationHandler.TracingContext;

import java.util.Objects;

class RevertingScope implements CurrentTraceContext.Scope {

    private final TracingObservationHandler.TracingContext tracingContext;

    private final CurrentTraceContext.Scope currentScope;

    private final CurrentTraceContext.Scope previousScope;

    RevertingScope(TracingContext tracingContext, Scope currentScope, Scope previousScope) {
        this.tracingContext = tracingContext;
        this.currentScope = currentScope;
        this.previousScope = previousScope;
    }

    @Override
    public void close() {
        this.currentScope.close();
        this.tracingContext.setScope(this.previousScope);
    }

    @Override
    public String toString() {
        return "RevertingScope{" + "tracingContext=" + tracingContext + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RevertingScope that = (RevertingScope) o;
        return Objects.equals(tracingContext, that.tracingContext) && Objects.equals(currentScope, that.currentScope)
                && Objects.equals(previousScope, that.previousScope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tracingContext, currentScope, previousScope);
    }

}
