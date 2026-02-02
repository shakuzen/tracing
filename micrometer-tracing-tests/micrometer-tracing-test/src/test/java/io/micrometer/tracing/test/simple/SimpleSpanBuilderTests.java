/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.simple;

import java.util.HashMap;
import java.util.Map;

import io.micrometer.tracing.Link;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import org.junit.jupiter.api.Test;

class SimpleSpanBuilderTests {

    @Test
    void should_build_a_span_using_builder() {
        SimpleTracer simpleTracer = new SimpleTracer();
        SimpleSpanBuilder builder = new SimpleSpanBuilder(simpleTracer);
        SimpleTraceContext context1 = new SimpleTraceContext();
        SimpleTraceContext context2 = new SimpleTraceContext();

        builder.name("foo")
            .kind(Span.Kind.CLIENT)
            .remoteIpAndPort("1.2.3.4", 80)
            .error(new Throwable())
            .event("foo event")
            .tag("tag", "value")
            .remoteServiceName("bar")
            .setNoParent()
            .addLink(new Link(context1))
            .addLink(new Link(context2, tags()))
            .start()
            .end();

        TracerAssert.assertThat(simpleTracer)
            .onlySpan()
            .hasNameEqualTo("foo")
            .hasRemoteServiceNameEqualTo("bar")
            .hasTag("tag", "value")
            .hasEventWithNameEqualTo("foo event")
            .hasIpThatIsNotBlank()
            .hasPortThatIsSet()
            .hasKindEqualTo(Span.Kind.CLIENT)
            .hasLink(new Link(context1))
            .hasLink(new Link(context2, tags()))
            .assertThatThrowable()
            .isInstanceOf(Throwable.class);
    }

    @Test
    void should_build_a_span_using_builder_with_parent() {
        SimpleTracer simpleTracer = new SimpleTracer();
        SimpleSpanBuilder builder = new SimpleSpanBuilder(simpleTracer);
        SimpleTraceContextBuilder ctxBuilder = new SimpleTraceContextBuilder();
        TraceContext parentCtx = ctxBuilder.spanId("spam").traceId("bar").sampled(true).build();

        builder.name("foo").setParent(parentCtx).start().end();

        TracerAssert.assertThat(simpleTracer)
            .onlySpan()
            .hasNameEqualTo("foo")
            .hasTraceIdEqualTo("bar")
            .hasParentIdEqualTo("spam");
    }

    private Map<String, Object> tags() {
        Map<String, Object> map = new HashMap<>();
        map.put("tag1", "value1");
        map.put("tag2", "value2");
        return map;
    }

}
