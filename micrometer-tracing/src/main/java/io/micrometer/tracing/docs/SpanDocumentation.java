/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.docs;

import io.micrometer.common.docs.KeyName;

/**
 * In order to describe your spans via e.g. enums instead of Strings you can use this
 * interface that returns all the characteristics of a span. In Micrometer Tracing we
 * analyze the sources and reuse this information to build a table of known spans, their
 * names, tags and events.
 * <p>
 * We can generate documentation for all created spans but certain requirements need to be
 * met
 *
 * <ul>
 * <li>Metrics are grouped within an enum - the enum implements the
 * {@link SpanDocumentation} interface</li>
 * <li>If the span contains {@link KeyName} then those need to be declared as nested
 * enums</li>
 * <li>If the span contains {@link EventValue} then those need to be declared as nested
 * enums</li>
 * <li>The {@link SpanDocumentation#getKeyNames()} need to call the nested enum's
 * {@code values()} method to retrieve the array of allowed keys</li>
 * <li>The {@link SpanDocumentation#getEvents()} need to call the nested enum's
 * {@code values()} method to retrieve the array of allowed events</li>
 * <li>Javadocs around enums will be used as description</li>
 * </ul>
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public interface SpanDocumentation {

    /**
     * Empty key names.
     */
    KeyName[] EMPTY_KEY_NAMES = new KeyName[0];

    /**
     * Empty values.
     */
    EventValue[] EMPTY_VALUES = new EventValue[0];

    /**
     * Span name.
     * @return metric name
     */
    String getName();

    /**
     * Builds a name from provided vars. Follows the
     * {@link String#format(String, Object...)} patterns.
     * @param vars variables to pass to {@link String#format(String, Object...)}
     * @return constructed name
     */
    default String getName(String... vars) {
        if (getName().contains("%s")) {
            return String.format(getName(), (Object[]) vars);
        }
        return getName();
    }

    /**
     * Allowed events.
     * @return allowed events
     */
    default EventValue[] getEvents() {
        return EMPTY_VALUES;
    }

    /**
     * Allowed key names.
     * @return allowed key names - if set will override any key names coming from
     * {@link SpanDocumentation#overridesDefaultSpanFrom()}
     */
    default KeyName[] getKeyNames() {
        return EMPTY_KEY_NAMES;
    }

    /**
     * Additional key names.
     * @return additional key names - if set will append any key names coming from
     * {@link SpanDocumentation#overridesDefaultSpanFrom()}
     */
    default KeyName[] getAdditionalKeyNames() {
        return EMPTY_KEY_NAMES;
    }

    /**
     * Override this when custom span should be documented instead of the default one.
     * @return {@link io.micrometer.observation.docs.ObservationDocumentation} for which
     * you don't want to create a default span documentation
     */
    default Enum<?> overridesDefaultSpanFrom() {
        return null;
    }

    /**
     * Returns required prefix to be there for tags. For example, {@code foo.} would
     * require the tags to have a {@code foo.} prefix like this: {@code foo.bar=true}.
     * @return required prefix
     */
    default String getPrefix() {
        return "";
    }

}
