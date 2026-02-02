/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.internal;

import io.micrometer.common.util.StringUtils;

/**
 * Utility class that provides the name in hyphen based notation.
 *
 * @author Adrian Cole
 * @since 1.0.0
 */
public final class SpanNameUtil {

    static final int MAX_NAME_LENGTH = 50;

    private SpanNameUtil() {

    }

    /**
     * Shortens the name of a span.
     * @param name name to shorten
     * @return shortened name
     */
    public static String shorten(String name) {
        if (!StringUtils.isNotBlank(name)) {
            return name;
        }
        int maxLength = Math.min(name.length(), MAX_NAME_LENGTH);
        return name.substring(0, maxLength);
    }

    /**
     * Converts the name to a lower hyphen version.
     * @param name name to change
     * @return changed name
     */
    public static String toLowerHyphen(String name) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) {
                    result.append('-');
                }
                result.append(Character.toLowerCase(c));
            }
            else {
                result.append(c);
            }
        }
        return SpanNameUtil.shorten(result.toString());
    }

}
