/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import io.micrometer.observation.transport.Kind;
import io.micrometer.tracing.http.HttpServerRequest;
import io.micrometer.tracing.http.HttpServerResponse;

import java.util.Collection;
import java.util.Collections;

/**
 * Brave implementation of a {@link HttpServerResponse}.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
class BraveHttpServerResponse implements HttpServerResponse {

    final brave.http.HttpServerResponse delegate;

    BraveHttpServerResponse(brave.http.HttpServerResponse delegate) {
        this.delegate = delegate;
    }

    static brave.http.HttpServerResponse toBrave(HttpServerResponse response) {
        if (response == null) {
            return null;
        }
        else if (response instanceof BraveHttpServerResponse) {
            return ((BraveHttpServerResponse) response).delegate;
        }
        return new brave.http.HttpServerResponse() {
            @Override
            public brave.http.HttpServerRequest request() {
                return BraveHttpServerRequest.toBrave(response.request());
            }

            @Override
            public Throwable error() {
                return response.error();
            }

            @Override
            public String method() {
                return response.method();
            }

            @Override
            public String route() {
                return response.route();
            }

            @Override
            public String toString() {
                return response.toString();
            }

            @Override
            public int statusCode() {
                return response.statusCode();
            }

            @Override
            public Object unwrap() {
                return response.unwrap();
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
    public HttpServerRequest request() {
        brave.http.HttpServerRequest request = this.delegate.request();
        if (request == null) {
            return null;
        }
        return new BraveHttpServerRequest(request);
    }

    @Override
    public Throwable error() {
        return this.delegate.error();
    }

}
