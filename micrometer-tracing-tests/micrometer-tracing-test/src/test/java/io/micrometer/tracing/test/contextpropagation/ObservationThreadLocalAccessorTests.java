/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.test.contextpropagation;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static reactor.core.observability.micrometer.Micrometer.observation;

class ObservationThreadLocalAccessorTests {

    static ObservationRegistry original = ObservationThreadLocalAccessor.getInstance().getObservationRegistry();

    @AfterEach
    void cleanup() {
        ObservationThreadLocalAccessor.getInstance().setObservationRegistry(original);
        Hooks.disableAutomaticContextPropagation();
    }

    @ParameterizedTest(
            name = "<{index}> Reactor propagation enabled <{0}>, ObservationRegistry in OTLA <{1}>, ObservationRegistry in TAP <{2}>")
    @MethodSource("argumentsForReactorMicrometerInterop")
    void monoWithoutException(boolean enableAutomaticPropagation, ObservationRegistry registryInOtla,
            ObservationRegistry registryInTap) {
        if (enableAutomaticPropagation) {
            Hooks.enableAutomaticContextPropagation();
        }
        else {
            Hooks.disableAutomaticContextPropagation();
        }

        if (registryInOtla != null) {
            ObservationThreadLocalAccessor.getInstance().setObservationRegistry(registryInOtla);
        }

        final Mono<String> response = Mono.fromFuture(CompletableFuture.supplyAsync(() -> "OK"))
            .name("mono-without-exception")
            .tap(observation(registryInTap))
            .thenReturn("OK");

        StepVerifier.create(response).expectNext("OK").expectComplete().verify();
    }

    private static Stream<Arguments> argumentsForReactorMicrometerInterop() {
        return Stream.of(Arguments.of(true, null, ObservationRegistry.NOOP),
                Arguments.of(true, observationRegistryWithHandler(), ObservationRegistry.NOOP),
                Arguments.of(true, ObservationRegistry.NOOP, observationRegistryWithHandler()),
                Arguments.of(true, observationRegistryWithHandler(), observationRegistryWithHandler()),
                Arguments.of(false, null, ObservationRegistry.NOOP),
                Arguments.of(false, observationRegistryWithHandler(), ObservationRegistry.NOOP),
                Arguments.of(false, ObservationRegistry.NOOP, observationRegistryWithHandler()),
                Arguments.of(false, observationRegistryWithHandler(), observationRegistryWithHandler()));
    }

    private static ObservationRegistry observationRegistryWithHandler() {
        ObservationRegistry registry = ObservationRegistry.create();
        registry.observationConfig().observationHandler(context -> true);
        return registry;
    }

}
