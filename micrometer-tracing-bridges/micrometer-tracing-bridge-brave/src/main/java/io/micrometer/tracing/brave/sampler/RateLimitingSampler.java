/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.sampler;

import java.util.function.Supplier;

import brave.sampler.Sampler;

/**
 * The rate-limited sampler allows you to choose an amount of traces to accept on a
 * per-second interval. The minimum number is 0 and the max is 2,147,483,647 (max int).
 * <p>
 * You can read more about it in {@link brave.sampler.RateLimitingSampler}
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
public class RateLimitingSampler extends Sampler {

    private final Sampler sampler;

    /**
     * Creates a new instance of {@link RateLimitingSampler}.
     * @param rate supplier of rate
     */
    public RateLimitingSampler(Supplier<Integer> rate) {
        this.sampler = brave.sampler.RateLimitingSampler.create(rateLimit(rate));
    }

    private Integer rateLimit(Supplier<Integer> rate) {
        return rate.get() != null ? rate.get() : 0;
    }

    @Override
    public boolean isSampled(long traceId) {
        return this.sampler.isSampled(traceId);
    }

}
