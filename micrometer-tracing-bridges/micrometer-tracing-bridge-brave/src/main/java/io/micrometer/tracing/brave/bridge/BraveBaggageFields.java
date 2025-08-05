/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.Baggage;

import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;

class BraveBaggageFields {

    private final List<AbstractMap.SimpleEntry<Baggage, String>> entries;

    BraveBaggageFields(List<AbstractMap.SimpleEntry<Baggage, String>> entries) {
        this.entries = entries;
    }

    List<AbstractMap.SimpleEntry<Baggage, String>> getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        return "BraveBaggageFields{" + "entries=" + entries + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BraveBaggageFields that = (BraveBaggageFields) o;
        return Objects.equals(entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }

}
