/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.observation.transport.Kind;
import io.micrometer.tracing.http.HttpRequest;

import java.util.Collection;
import java.util.Collections;

/**
 * Brave implementation of a {@link HttpRequest}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
class BraveHttpRequest implements HttpRequest {

    final brave.http.HttpRequest delegate;

    BraveHttpRequest(brave.http.HttpRequest delegate) {
        this.delegate = delegate;
    }

    static brave.http.HttpRequest toBrave(HttpRequest httpRequest) {
        return ((BraveHttpRequest) httpRequest).delegate;
    }

    static HttpRequest fromBrave(brave.http.HttpRequest httpRequest) {
        return new BraveHttpRequest(httpRequest);
    }

    @Override
    public String method() {
        return this.delegate.method();
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

    @Override
    public Collection<String> headerNames() {
        // this is unused by Brave
        return Collections.emptyList();
    }

    @Override
    public Kind kind() {
        return Kind.valueOf(this.delegate.spanKind().name());
    }

    @Override
    public Object unwrap() {
        return this.delegate.unwrap();
    }

}
