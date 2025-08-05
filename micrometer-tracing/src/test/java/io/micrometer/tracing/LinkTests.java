/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

class LinkTests {

    @Test
    void equals_should_work_with_trace_context() {
        TraceContext traceContext = BDDMockito.mock(TraceContext.class);
        Link link1 = new Link(traceContext);
        Link link2 = new Link(traceContext);

        BDDAssertions.then(link1).isEqualTo(link2);

        link1 = new Link(traceContext);
        link2 = new Link(TraceContext.NOOP);

        BDDAssertions.then(link1).isNotEqualTo(link2);
    }

    @Test
    void equals_should_work_with_tags() {
        TraceContext traceContext = BDDMockito.mock(TraceContext.class);
        Link link1 = new Link(traceContext, tags1());
        Link link2 = new Link(traceContext, tags1());

        BDDAssertions.then(link1).isEqualTo(link2);

        link1 = new Link(traceContext, tags1());
        link2 = new Link(traceContext, tags2());

        BDDAssertions.then(link1).isNotEqualTo(link2);
    }

    @Test
    void hashcode_should_work_with_trace_context() {
        TraceContext traceContext = BDDMockito.mock(TraceContext.class);
        Link link1 = new Link(traceContext);
        Link link2 = new Link(traceContext);

        BDDAssertions.then(link1.hashCode()).isEqualTo(link2.hashCode());

        link1 = new Link(traceContext);
        link2 = new Link(TraceContext.NOOP);

        BDDAssertions.then(link1.hashCode()).isNotEqualTo(link2.hashCode());
    }

    @Test
    void hashchode_should_work_with_tags() {
        TraceContext traceContext = BDDMockito.mock(TraceContext.class);
        Link link1 = new Link(traceContext, tags1());
        Link link2 = new Link(traceContext, tags1());

        BDDAssertions.then(link1.hashCode()).isEqualTo(link2.hashCode());

        link1 = new Link(traceContext, tags1());
        link2 = new Link(traceContext, tags2());

        BDDAssertions.then(link1.hashCode()).isNotEqualTo(link2.hashCode());
    }

    private Map<String, Object> tags1() {
        Map<String, Object> map = new HashMap<>();
        map.put("tag1", "value1");
        map.put("tag2", "value2");
        return map;
    }

    private Map<String, Object> tags2() {
        Map<String, Object> map = new HashMap<>();
        map.put("tag3", "value3");
        map.put("tag4", "value4");
        return map;
    }

}
