/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.reporter.zipkin;

import java.util.concurrent.TimeUnit;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@WireMockTest
class ZipkinBraveSetupTests {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(ZipkinBraveSetupTests.class);

    ObservationRegistry registry = ObservationRegistry.create();

    @Test
    void should_register_a_span_in_zipkin(WireMockRuntimeInfo wmri) throws InterruptedException {
        ZipkinBraveSetup setup = ZipkinBraveSetup.builder().zipkinUrl(wmri.getHttpBaseUrl()).register(this.registry);

        ZipkinBraveSetup.run(setup, __ -> {
            Observation sample = Observation.start("the-name", this.registry);
            try (Observation.Scope scope = sample.openScope()) {
                log.info("New sample created");
            }
            sample.stop();
        });

        Awaitility.await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> wmri.getWireMock().verifyThat(anyRequestedFor(urlPathEqualTo("/api/v2/spans"))));
    }

}
