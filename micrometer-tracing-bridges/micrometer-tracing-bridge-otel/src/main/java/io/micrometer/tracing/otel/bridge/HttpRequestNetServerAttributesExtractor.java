/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.common.lang.Nullable;
import io.micrometer.tracing.http.HttpServerRequest;
import io.micrometer.tracing.http.HttpServerResponse;
import io.opentelemetry.instrumentation.api.instrumenter.net.NetServerAttributesGetter;

import java.net.URI;

/**
 * Extracts OpenTelemetry network semantic attributes value for server http spans.
 *
 * @author Nikita Salnikov-Tarnovski
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
class HttpRequestNetServerAttributesExtractor
        implements NetServerAttributesGetter<HttpServerRequest, HttpServerResponse> {

    @Nullable
    @Override
    public String getTransport(HttpServerRequest httpRequest) {
        String url = httpRequest.url();
        if (url == null) {
            return null;
        }
        URI uri = URI.create(url);
        return uri.getScheme();
    }

    @Nullable
    @Override
    public String getServerAddress(HttpServerRequest httpServerRequest) {
        String url = httpServerRequest.url();
        if (url == null) {
            return null;
        }
        URI uri = URI.create(url);
        return uri.getHost();
    }

    @Nullable
    @Override
    public Integer getServerPort(HttpServerRequest httpServerRequest) {
        return httpServerRequest.remotePort();
    }

    @Nullable
    @Override
    public String getClientSocketAddress(HttpServerRequest httpServerRequest,
            @Nullable HttpServerResponse httpServerResponse) {
        return httpServerRequest.remoteIp();
    }

    @Nullable
    @Override
    public Integer getClientSocketPort(HttpServerRequest httpServerRequest,
            @Nullable HttpServerResponse httpServerResponse) {
        return httpServerRequest.remotePort();
    }

}
