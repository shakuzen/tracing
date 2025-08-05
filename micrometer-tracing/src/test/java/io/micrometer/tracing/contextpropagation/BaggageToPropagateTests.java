/**
 * Copyright © 2024 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2024 the original author or authors.
 */
package io.micrometer.tracing.contextpropagation;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;

class BaggageToPropagateTests {

    @Test
    void should_throw_an_exception_when_even_number_of_args() {
        thenThrownBy(() -> new BaggageToPropagate("foo")).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("You need an even number of baggage entries. You have [1] entries");
    }

    @Test
    void should_set_baggage_to_propagate_entries_with_a_map() {
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");

        then(new BaggageToPropagate(map).getBaggage()).containsExactlyEntriesOf(map);
    }

    @Test
    void should_set_baggage_to_propagate_entries_with_varrgs() {
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("baz", "bar2");

        then(new BaggageToPropagate("foo", "bar", "baz", "bar2").getBaggage()).containsExactlyEntriesOf(map);
    }

}
