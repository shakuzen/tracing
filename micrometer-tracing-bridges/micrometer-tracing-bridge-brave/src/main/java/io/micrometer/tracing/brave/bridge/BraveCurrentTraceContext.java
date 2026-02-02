/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.common.lang.Nullable;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Brave implementation of a {@link CurrentTraceContext}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class BraveCurrentTraceContext implements CurrentTraceContext {

    final brave.propagation.CurrentTraceContext delegate;

    /**
     * Creates a new instance of {@link BraveCurrentTraceContext}.
     * @param delegate Brave delegate
     */
    public BraveCurrentTraceContext(brave.propagation.CurrentTraceContext delegate) {
        this.delegate = delegate;
    }

    /**
     * Converts from Tracing to Brave.
     * @param context Tracing delegate
     * @return converted version
     */
    public static brave.propagation.CurrentTraceContext toBrave(CurrentTraceContext context) {
        return ((BraveCurrentTraceContext) context).delegate;
    }

    /**
     * Converts from Brave to Tracing.
     * @param context Brave delegate
     * @return converted version
     */
    public static CurrentTraceContext fromBrave(brave.propagation.CurrentTraceContext context) {
        return new BraveCurrentTraceContext(context);
    }

    @Override
    @Nullable
    public TraceContext context() {
        brave.propagation.TraceContext context = this.delegate.get();
        if (context == null) {
            return null;
        }
        return new io.micrometer.tracing.brave.bridge.BraveTraceContext(context);
    }

    @Override
    public Scope newScope(@Nullable TraceContext context) {
        return new BraveScope(
                this.delegate.newScope(io.micrometer.tracing.brave.bridge.BraveTraceContext.toBrave(context)));
    }

    @Override
    public Scope maybeScope(@Nullable TraceContext context) {
        return new BraveScope(
                this.delegate.maybeScope(io.micrometer.tracing.brave.bridge.BraveTraceContext.toBrave(context)));
    }

    @Override
    public <C> Callable<C> wrap(Callable<C> task) {
        return this.delegate.wrap(task);
    }

    @Override
    public Runnable wrap(Runnable task) {
        return this.delegate.wrap(task);
    }

    @Override
    public Executor wrap(Executor delegate) {
        return this.delegate.executor(delegate);
    }

    @Override
    public ExecutorService wrap(ExecutorService delegate) {
        return this.delegate.executorService(delegate);
    }

}

class BraveScope implements CurrentTraceContext.Scope {

    private final brave.propagation.CurrentTraceContext.Scope delegate;

    BraveScope(brave.propagation.CurrentTraceContext.Scope delegate) {
        this.delegate = delegate;
    }

    @Override
    public void close() {
        this.delegate.close();
    }

}
