/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.tracing.http.HttpClientRequest;

import java.util.Collection;
import java.util.Collections;

/**
 * Brave implementation of a {@link HttpClientRequest}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
class BraveHttpClientRequest implements HttpClientRequest {

    final brave.http.HttpClientRequest delegate;

    BraveHttpClientRequest(brave.http.HttpClientRequest delegate) {
        this.delegate = delegate;
    }

    static brave.http.HttpClientRequest toBrave(HttpClientRequest httpClientRequest) {
        if (httpClientRequest instanceof BraveHttpClientRequest) {
            return ((BraveHttpClientRequest) httpClientRequest).delegate;
        }
        return new brave.http.HttpClientRequest() {

            @Override
            public Object unwrap() {
                return httpClientRequest.unwrap();
            }

            @Override
            public String method() {
                return httpClientRequest.method();
            }

            @Override
            public String path() {
                return httpClientRequest.path();
            }

            @Override
            public String url() {
                return httpClientRequest.url();
            }

            @Override
            public String header(String name) {
                return httpClientRequest.header(name);
            }

            @Override
            public void header(String name, String value) {
                httpClientRequest.header(name, value);
            }
        };
    }

    @Override
    public String method() {
        return this.delegate.method();
    }

    @Override
    public String route() {
        return this.delegate.route();
    }

    @Override
    public Object unwrap() {
        return this.delegate.unwrap();
    }

    @Override
    public Collection<String> headerNames() {
        // this is unused by Brave
        return Collections.emptyList();
    }

    @Override
    public void header(String name, String value) {
        this.delegate.header(name, value);
    }

    @Override
    public String path() {
        return this.delegate.path();
    }

    @Override
    public String url() {
        return this.delegate.url();
    }

    @Override
    public String header(String name) {
        return this.delegate.header(name);
    }

}
