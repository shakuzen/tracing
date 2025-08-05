/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.tracing.SamplerFunction;
import io.micrometer.tracing.http.HttpRequest;

import java.util.regex.Pattern;

/**
 * Decides if sampling should take place for the given request.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
public class SkipPatternSampler implements SamplerFunction<HttpRequest> {

    private final Pattern pattern;

    /**
     * Creates a new instance of {@link SkipPatternSampler}.
     * @param pattern skip pattern
     */
    public SkipPatternSampler(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public final Boolean trySample(HttpRequest request) {
        String url = request.path();
        boolean shouldSkip = this.pattern.matcher(url).matches();
        if (shouldSkip) {
            return false;
        }
        return null;
    }

}
