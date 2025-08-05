/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import brave.Tracing;
import brave.http.HttpClientHandler;
import brave.http.HttpTracing;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class BraveHttpClientHandlerTests {

    Tracing tracing = Tracing.newBuilder().build();

    @AfterEach
    void cleanup() {
        tracing.close();
    }

    @Test
    void should_not_throw_exception_when_response_null() {
        brave.http.HttpClientHandler<brave.http.HttpClientRequest, brave.http.HttpClientResponse> delegate = HttpClientHandler
            .create(HttpTracing.newBuilder(tracing).build());
        BraveHttpClientHandler handler = new BraveHttpClientHandler(delegate);

        handler.handleReceive(null, new BraveSpan(tracing.currentTracer().nextSpan()));
    }

}
