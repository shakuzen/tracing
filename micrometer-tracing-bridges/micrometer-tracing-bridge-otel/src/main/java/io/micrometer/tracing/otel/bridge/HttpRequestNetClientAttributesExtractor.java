/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.otel.bridge;

import io.micrometer.common.lang.Nullable;
import io.micrometer.tracing.http.HttpClientRequest;
import io.micrometer.tracing.http.HttpClientResponse;
import io.opentelemetry.instrumentation.api.instrumenter.net.NetClientAttributesGetter;

/**
 * Extracts OpenTelemetry network semantic attributes value for client http spans.
 *
 * @author Nikita Salnikov-Tarnovski
 * @deprecated scheduled for removal in 1.4.0
 */
@Deprecated
class HttpRequestNetClientAttributesExtractor
        implements NetClientAttributesGetter<HttpClientRequest, HttpClientResponse> {

    @Nullable
    @Override
    public String getTransport(HttpClientRequest httpClientRequest, @Nullable HttpClientResponse httpClientResponse) {
        return null;
    }

    @Nullable
    @Override
    public String getServerAddress(HttpClientRequest httpClientRequest) {
        return null;
    }

    @Nullable
    @Override
    public Integer getServerPort(HttpClientRequest httpClientRequest) {
        return httpClientRequest == null ? null : httpClientRequest.remotePort();
    }

}
