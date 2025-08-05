/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.observation.transport.Kind;
import io.micrometer.tracing.http.HttpRequest;
import io.micrometer.tracing.http.HttpResponse;

import java.util.Collection;
import java.util.Collections;

/**
 * Brave implementation of a {@link HttpResponse}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
class BraveHttpResponse implements HttpResponse {

    final brave.http.HttpResponse delegate;

    BraveHttpResponse(brave.http.HttpResponse delegate) {
        this.delegate = delegate;
    }

    static brave.http.HttpResponse toBrave(HttpResponse httpResponse) {
        return ((BraveHttpResponse) httpResponse).delegate;
    }

    static HttpResponse fromBrave(brave.http.HttpResponse httpResponse) {
        return new BraveHttpResponse(httpResponse);
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
    public int statusCode() {
        return this.delegate.statusCode();
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
    public Kind kind() {
        return Kind.valueOf(this.delegate.spanKind().name());
    }

    @Override
    public HttpRequest request() {
        brave.http.HttpRequest request = this.delegate.request();
        if (request == null) {
            return null;
        }
        return new BraveHttpRequest(request);
    }

    @Override
    public Throwable error() {
        return this.delegate.error();
    }

}
