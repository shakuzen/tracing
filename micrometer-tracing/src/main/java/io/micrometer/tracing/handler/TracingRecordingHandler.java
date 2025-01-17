/*
 * Copyright 2021-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micrometer.tracing.handler;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.TimerRecordingHandler;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

/**
 * Marker interface for tracing listeners.
 *
 * @author Marcin Grzejszczak
 * @param <T> type of event
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface TracingRecordingHandler<T extends Timer.HandlerContext>
        extends TimerRecordingHandler<T> {

    default void tagSpan(T context, Span span) {
        context.getAllTags().forEach(tag -> span.tag(tag.getKey(), tag.getValue()));
    }

    /**
     * Puts the span in scope.
     *
     * @param context recording with context containing scope
     */
    @Override
    default void onScopeOpened(Timer.Sample sample, T context) {
        Span span = getTracingContext(context).getSpan();
        if (span == null) {
            return;
        }
        CurrentTraceContext.Scope scope = getTracer().currentTraceContext()
                .maybeScope(span.context());
        getTracingContext(context).setSpanAndScope(span, scope);
    }

    /**
     * Cleans the scope present in the context.
     *
     * @param context recording with context containing scope
     */
    @Override
    default void onScopeClosed(Timer.Sample sample, T context) {
        TracingContext tracingContext = getTracingContext(context);
        tracingContext.getScope().close();
    }

    default TracingContext getTracingContext(T context) {
        // maybe consider returning a null ?
        return context.computeIfAbsent(TracingContext.class,
                clazz -> new TracingContext());
    }

    @Override
    default boolean supportsContext(Timer.HandlerContext context) {
        return context != null;
    }

    /**
     * Returns the {@link Tracer}.
     *
     * @return tracer
     */
    Tracer getTracer();

    /**
     * Basic tracing context.
     *
     * @author Marcin Grzejszczak
     * @since 1.0.0
     */
    class TracingContext {

        private Span span;

        private CurrentTraceContext.Scope scope;

        /**
         * Returns the span.
         *
         * @return span
         */
        Span getSpan() {
            return this.span;
        }

        /**
         * Sets the span.
         *
         * @param span span to set
         */
        void setSpan(Span span) {
            this.span = span;
        }

        /**
         * Returns the scope of the span.
         *
         * @return scope of the span
         */
        CurrentTraceContext.Scope getScope() {
            return this.scope;
        }

        /**
         * Sets the current trace context scope.
         *
         * @param scope scope to set
         */
        void setScope(CurrentTraceContext.Scope scope) {
            this.scope = scope;
        }

        /**
         * Convenience method to set both span and scope.
         *
         * @param span span to set
         * @param scope scope to set
         */
        void setSpanAndScope(Span span, CurrentTraceContext.Scope scope) {
            setSpan(span);
            setScope(scope);
        }

    }

}
