/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.SamplerFunction;
import io.micrometer.tracing.http.HttpRequest;

/**
 * Brave implementation of a {@link SamplerFunction}.
 *
 * @param <T> type of the input, for example a request or method
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
@SuppressWarnings("unchecked")
public final class BraveSamplerFunction<T> implements SamplerFunction<T> {

    final brave.sampler.SamplerFunction<T> samplerFunction;

    /**
     * Creates a new instance of {@link BraveSamplerFunction}.
     * @param samplerFunction Brave {@link SamplerFunction}
     */
    public BraveSamplerFunction(brave.sampler.SamplerFunction<T> samplerFunction) {
        DeprecatedClassLogger.logWarning(getClass());
        this.samplerFunction = samplerFunction;
    }

    /**
     * Converts from Tracing to Brave.
     * @param samplerFunction Tracing version
     * @return Brave version
     * @deprecated scheduled for removal in 1.4.0
     */
    @Deprecated
    public static brave.sampler.SamplerFunction<brave.http.HttpRequest> toHttpBrave(
            SamplerFunction<HttpRequest> samplerFunction) {
        DeprecatedClassLogger.logWarning(BraveSamplerFunction.class);
        return arg -> samplerFunction.trySample(BraveHttpRequest.fromBrave(arg));
    }

    @Override
    public Boolean trySample(T arg) {
        DeprecatedClassLogger.logWarning(getClass());
        return this.samplerFunction.trySample(arg);
    }

}
