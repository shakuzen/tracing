/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.common.util.StringUtils;
import io.micrometer.tracing.Link;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.internal.EncodingUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class LinkUtils {

    private static final String LINKS_PREFIX = "links[";

    private static final String TRACE_ID = "links[%s].traceId";

    private static final String SPAN_ID = "links[%s].spanId";

    private static final String TAG = "links[%s].tags[%s]";

    private static final Pattern LINKS_ID = Pattern.compile("^links\\[(.*)]\\..*$");

    private static final Pattern TAG_KEY = Pattern.compile("^links\\[.*]\\.tags\\[(.*)]$");

    static boolean isApplicable(Map.Entry<String, String> entry) {
        return entry.getKey().startsWith(LINKS_PREFIX);
    }

    static int linkGroup(Map.Entry<String, String> entry) {
        Matcher matcher = LINKS_ID.matcher(entry.getKey());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    static String traceIdKey(long index) {
        return String.format(TRACE_ID, index);
    }

    static String spanIdKey(long index) {
        return String.format(SPAN_ID, index);
    }

    static String tagKey(long index, String tagKey) {
        return String.format(TAG, index, tagKey);
    }

    static int nextIndex(Map<String, String> tags) {
        return tags.entrySet()
            .stream()
            .filter(LinkUtils::isApplicable)
            .collect(Collectors.groupingBy(LinkUtils::linkGroup))
            .keySet()
            .size();
    }

    static Link toLink(List<Map.Entry<String, String>> groupedTags) {
        String traceId = "";
        String spanId = "";
        Map<String, Object> tags = new HashMap<>();
        for (Map.Entry<String, String> groupedTag : groupedTags) {
            if (groupedTag.getKey().endsWith(".traceId")) {
                traceId = groupedTag.getValue();
            }
            else if (groupedTag.getKey().endsWith(".spanId")) {
                spanId = groupedTag.getValue();
            }
            else if (groupedTag.getKey().contains("tags")) {
                String tagKey = tagKeyNameFromString(groupedTag.getKey());
                if (tagKey != null) {
                    tags.put(tagKey, groupedTag.getValue());
                }
            }
        }
        if (StringUtils.isNotBlank(traceId)) {
            brave.propagation.TraceContext.Builder newBuilder = traceId(brave.propagation.TraceContext.newBuilder(),
                    traceId);
            TraceContext traceContext = new BraveTraceContext(newBuilder.spanId(spanId(spanId)).build());
            return new Link(traceContext, tags);
        }
        return null;
    }

    static brave.propagation.TraceContext.Builder traceId(brave.propagation.TraceContext.Builder delegate,
            String traceId) {
        long[] fromString = EncodingUtils.fromString(traceId);
        if (fromString.length == 2) {
            delegate.traceIdHigh(fromString[0]);
            delegate.traceId(fromString[1]);
        }
        else {
            delegate.traceId(fromString[0]);
        }
        return delegate;
    }

    static long spanId(String spanId) {
        long[] fromString = EncodingUtils.fromString(spanId);
        return fromString[fromString.length == 2 ? 1 : 0];
    }

    static String tagKeyNameFromString(String tag) {
        Matcher matcher = TAG_KEY.matcher(tag);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

}
