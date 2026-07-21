/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.brave.bridge;

import brave.Tracing;
import brave.baggage.*;
import brave.context.slf4j.MDCScopeDecorator;
import brave.internal.baggage.BaggageFields;
import brave.propagation.*;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.transport.ReceiverContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import io.micrometer.tracing.handler.PropagatingReceiverTracingObservationHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Test taken from OpenTelemetry.
 */
class W3CBaggagePropagatorTest {

    W3CBaggagePropagator propagator = new W3CBaggagePropagator(new BraveBaggageManager(), Collections.emptyList());

    @Test
    void fields() {
        assertThat(propagator.keys()).containsExactly("baggage");
    }

    @Test
    void extract_noBaggageHeader() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        assertThat(contextWithBaggage).isEqualTo(contextWithBraveBaggageFields(context));
    }

    private TraceContextOrSamplingFlags contextWithBraveBaggageFields(TraceContextOrSamplingFlags context) {
        return context.toBuilder().addExtra(new BraveBaggageFields(Collections.emptyList())).build();
    }

    @Test
    void extract_emptyBaggageHeader() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        assertThat(contextWithBaggage).isEqualTo(contextWithBraveBaggageFields(context));
    }

    @Test
    void extract_metadataOnlyBaggageHeader() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", ";metadata");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);
        assertThat(baggageEntries(contextWithBaggage)).isEmpty();
    }

    @Test
    void extract_noValueBaggageHeader() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "a=");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);
        assertThat(baggageEntries(contextWithBaggage)).isEmpty();
    }

    @Test
    void extract_noValueButMetadataBaggageHeader() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "a=;metadata");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);
        assertThat(baggageEntries(contextWithBaggage)).isEmpty();
    }

    @Test
    void extract_keyValuesNotinPairBaggageHeader() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "a=b,oops,c=,=d,=");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);
        assertThat(baggageEntries(contextWithBaggage)).containsExactly(entry("a", "b"));
    }

    @Test
    void extract_singleEntry() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "key=value");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).hasSize(1).containsEntry("key", "value");
    }

    private TraceContextOrSamplingFlags context() {
        return TraceContextOrSamplingFlags
            .newBuilder(TraceContext.newBuilder().traceId(1L).spanId(2L).sampled(true).build())
            .build();
    }

    @Test
    void extract_multiEntry() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "key1=value1,key2=value2");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).hasSize(2).containsEntry("key1", "value1").containsEntry("key2", "value2");
    }

    @Test
    void extract_duplicateKeys() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "key=value1,key=value2");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).hasSize(1).containsEntry("key", "value2");
    }

    @Test
    void extract_fullComplexities() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage",
                "key1= value1; metadata-key = value; othermetadata, " + "key2 =value2 , key3 =\tvalue3 ; ");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).hasSize(3)
            .containsEntry("key1", "value1")
            .containsEntry("key2", "value2")
            .containsEntry("key3", "value3");
    }

    private Map<String, String> baggageEntries(TraceContextOrSamplingFlags flags) {
        if (flags.context().extra().isEmpty() || !(flags.context().extra().get(0) instanceof BraveBaggageFields)) {
            throw new AssertionError("Extra doesn't contain BraveBaggageFields as first entry");
        }
        BraveBaggageFields fields = (BraveBaggageFields) flags.context().extra().get(0);
        return fields.getEntries()
            .stream()
            .collect(Collectors.toMap(e -> e.getKey().name(), AbstractMap.SimpleEntry::getValue, (o, o2) -> o2));
    }

    /**
     * It would be cool if we could replace this with a fuzzer to generate tons of crud
     * data, to make sure we don't blow up with it.
     */
    @Test
    void extract_invalidHeader() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "key1= v;alsdf;-asdflkjasdf===asdlfkjadsf ,,a sdf9asdf-alue1; metadata-key = "
                + "value; othermetadata, key2 =value2 , key3 =\tvalue3 ; ");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);
        assertThat(baggageEntries(contextWithBaggage)).containsExactly(entry("key1", "v"), entry("key2", "value2"),
                entry("key3", "value3"));
    }

    @Test
    void inject_noBaggage() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();

        propagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(context.context(), carrier);

        assertThat(carrier).isEmpty();
    }

    @Test
    void inject() {
        TraceContextOrSamplingFlags.Builder builder = context().toBuilder();
        BaggageField nometa = BaggageField.create("nometa");
        BaggageField meta = BaggageField.create("meta");
        builder.addExtra(BaggageFields.newFactory(Arrays.asList(nometa, meta), 10).create());
        TraceContextOrSamplingFlags context = builder.build();
        nometa.updateValue(context, "nometa-value");
        meta.updateValue(context, "meta-value;somemetadata; someother=foo");
        Map<String, String> carrier = new HashMap<>();

        propagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(context.context(), carrier);

        assertThat(carrier).containsExactlyInAnyOrderEntriesOf(
                singletonMap("baggage", "nometa=nometa-value,meta=meta-value%3Bsomemetadata%3B%20someother%3Dfoo"));
    }

    @Test
    void inject_percentEncodesSpecialCharacters() {
        TraceContextOrSamplingFlags.Builder builder = context().toBuilder();
        BaggageField field = BaggageField.create("my-key");
        builder.addExtra(BaggageFields.newFactory(Arrays.asList(field), 10).create());
        TraceContextOrSamplingFlags context = builder.build();
        field.updateValue(context, "value,with=delimiters;and spaces");
        Map<String, String> carrier = new HashMap<>();

        propagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(context.context(), carrier);

        assertThat(carrier).containsEntry("baggage", "my-key=value%2Cwith%3Ddelimiters%3Band%20spaces");
    }

    @Test
    void extract_neutralizesDelimiterInjection() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "my-key=value%2Ctenant-id%3Dadmin");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).containsEntry("my-key", "value,tenant-id=admin").doesNotContainKey("tenant-id");
    }

    @Test
    void exploitInTheWildScenario() {
        // --- STEP 1: Service A (Vulnerable) ---
        // Service A only registers "req-id" as a remote field. It does NOT register or
        // trust "tenant-id" from clients.
        BraveBaggageManager serviceABaggageManager = new BraveBaggageManager(Collections.emptyList(),
                Collections.singletonList("req-id"));
        W3CBaggagePropagator serviceAPropagator = new W3CBaggagePropagator(serviceABaggageManager,
                Collections.emptyList());

        // An attacker sends a request to Service A with an input parameter that Service A
        // puts into its "req-id" baggage.
        // For example, the attacker passes: "123,tenant-id=admin" as the request ID.
        String attackerControlledInput = "123,tenant-id=admin";

        TraceContextOrSamplingFlags.Builder serviceAContextBuilder = context().toBuilder();
        BaggageField reqIdField = BaggageField.create("req-id");
        serviceAContextBuilder.addExtra(BaggageFields.newFactory(Collections.singletonList(reqIdField), 10).create());
        TraceContextOrSamplingFlags serviceAContext = serviceAContextBuilder.build();

        // Service A sets the baggage value from the attacker-controlled input
        reqIdField.updateValue(serviceAContext, attackerControlledInput);

        // Service A makes an HTTP call to Service B, injecting the baggage header.
        Map<String, String> serviceAOutboundHeaders = new HashMap<>();
        serviceAPropagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(serviceAContext.context(), serviceAOutboundHeaders);

        // --- STEP 2: Service B (Downstream) ---
        // Service B is a downstream service that trusts "tenant-id" baggage if it comes
        // from an upstream service (like Service A).
        BraveBaggageManager serviceBBaggageManager = new BraveBaggageManager(Collections.emptyList(),
                Arrays.asList("req-id", "tenant-id"));
        W3CBaggagePropagator serviceBPropagator = new W3CBaggagePropagator(serviceBBaggageManager,
                Collections.emptyList());

        // Service B receives the headers from Service A and extracts the baggage.
        TraceContextOrSamplingFlags serviceBContext = serviceBPropagator.contextWithBaggage(serviceAOutboundHeaders,
                context(), Map::get);
        Map<String, String> serviceBBaggageEntries = baggageEntries(serviceBContext);

        // --- STEP 3: Verify the Vulnerability / Protection ---
        // Under the fixed code, the attacker's injected "tenant-id=admin" is safely
        // percent-encoded
        // and does NOT get parsed as a separate baggage key by Service B.
        assertThat(serviceBBaggageEntries).containsEntry("req-id", "123,tenant-id=admin")
            .doesNotContainKey("tenant-id");
    }

    @Test
    void blackBoxExploitPoC() throws Exception {
        // We start a lightweight local HTTP server to act as our microservices
        com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer
            .create(new java.net.InetSocketAddress(0), 0);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());

        // This list will capture the headers received by the downstream Service B
        java.util.List<String> capturedBaggageHeaders = new java.util.concurrent.CopyOnWriteArrayList<>();

        // Configure Service A (Vulnerable Application)
        // Service A only registers "req-id" as a remote field. It does NOT register or
        // trust "tenant-id" from clients.
        BraveBaggageManager serviceABaggageManager = new BraveBaggageManager(Collections.emptyList(),
                Collections.singletonList("req-id"));
        W3CBaggagePropagator serviceAPropagator = new W3CBaggagePropagator(serviceABaggageManager,
                Collections.emptyList());

        // Handler for Service A (Vulnerable)
        server.createContext("/service-a", exchange -> {
            try {
                // Read the query parameter "reqId" representing attacker-controlled input
                String query = exchange.getRequestURI().getQuery();
                String attackerInput = query.split("reqId=")[1];

                // Create a trace context and set the baggage field
                TraceContextOrSamplingFlags.Builder contextBuilder = TraceContextOrSamplingFlags
                    .newBuilder(TraceContext.newBuilder().traceId(1L).spanId(2L).sampled(true).build());
                BaggageField reqIdField = BaggageField.create("req-id");
                contextBuilder.addExtra(BaggageFields.newFactory(Collections.singletonList(reqIdField), 10).create());
                TraceContextOrSamplingFlags context = contextBuilder.build();

                reqIdField.updateValue(context, attackerInput);

                // Now Service A makes a downstream HTTP call to Service B
                java.net.URL serviceBUrl = new java.net.URL(
                        "http://localhost:" + server.getAddress().getPort() + "/service-b");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) serviceBUrl.openConnection();
                conn.setRequestMethod("GET");

                // Inject the baggage header using our propagator
                Propagation.Setter<java.net.HttpURLConnection, String> setter = (carrier, key, val) -> carrier
                    .setRequestProperty(key, val);
                serviceAPropagator.injector(setter).inject(context.context(), conn);

                // Send the request and read response to complete the call
                conn.getResponseCode();
                conn.disconnect();

                // Respond to the attacker
                byte[] response = "OK".getBytes();
                exchange.sendResponseHeaders(200, response.length);
                try (java.io.OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
            finally {
                exchange.close();
            }
        });

        // Handler for Service B (Downstream Service)
        server.createContext("/service-b", exchange -> {
            try {
                // Capture the incoming "baggage" header exactly as it was received over
                // the wire
                String baggageHeader = exchange.getRequestHeaders().getFirst("baggage");
                if (baggageHeader != null) {
                    capturedBaggageHeaders.add(baggageHeader);
                }
                exchange.sendResponseHeaders(200, -1);
            }
            finally {
                exchange.close();
            }
        });

        server.start();

        try {
            // --- THE ATTACK ---
            // The attacker sends a request to Service A, trying to inject
            // "tenant-id=admin" via the "reqId" parameter.
            // Note: The attacker does not have access to headers or code of Service B.
            // They only send an HTTP request to Service A.
            String attackerPayload = "123,tenant-id=admin";
            java.net.URL url = new java.net.URL("http://localhost:" + server.getAddress().getPort()
                    + "/service-a?reqId=" + java.net.URLEncoder.encode(attackerPayload, "UTF-8"));
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response from Service A
            int responseCode = conn.getResponseCode();
            assertThat(responseCode).isEqualTo(200);
            conn.disconnect();

            // --- VERIFICATION ---
            // Verify that the outbound baggage header sent from Service A to Service B
            // was safely percent-encoded.
            assertThat(capturedBaggageHeaders).hasSize(1);
            String receivedHeader = capturedBaggageHeaders.get(0);

            // Under the fixed code, the comma and equals sign are percent-encoded:
            // "req-id=123%2Ctenant-id%3Dadmin"
            // If the vulnerability were present, it would be:
            // "req-id=123,tenant-id=admin"
            assertThat(receivedHeader).isEqualTo("req-id=123%2Ctenant-id%3Dadmin");

        }
        finally {
            server.stop(0);
        }
    }

    @Test
    void works_with_scopes_and_observations() {
        // Baggage
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "key=value,key2=value2");
        carrier.put("traceparent", "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01");
        // carrier.put("key", "value");
        // carrier.put("key2", "value2");
        // carrier.put("b3", "00f067aa0ba902b7-00f067aa0ba902b7-1");

        // Brave with W3C
        BraveBaggageManager braveBaggageManager = new BraveBaggageManager();
        ThreadLocalCurrentTraceContext currentTraceContext = ThreadLocalCurrentTraceContext.newBuilder()
            .addScopeDecorator(correlationScopeDecorator(mdcCorrelationScopeDecoratorBuilder()))
            .build();
        try (Tracing tracing = Tracing.newBuilder()
            .propagationFactory(micrometerTracingPropagationWithBaggage(w3cPropagationFactory(braveBaggageManager)))
            // .propagationFactory(micrometerTracingPropagationWithBaggage(b3PropagationFactory()))
            .currentTraceContext(currentTraceContext)
            .build()) {
            Tracer tracer = new BraveTracer(tracing.tracer(),
                    new BraveCurrentTraceContext(tracing.currentTraceContext()), braveBaggageManager);
            BravePropagator bravePropagator = new BravePropagator(tracing);

            // Observation
            TestObservationRegistry registry = TestObservationRegistry.create();
            registry.observationConfig()
                .observationHandler(new ObservationHandler.FirstMatchingCompositeObservationHandler(
                        new PropagatingReceiverTracingObservationHandler<>(tracer, bravePropagator),
                        new DefaultTracingObservationHandler(tracer)));

            ReceiverContext<Map<String, String>> receiverContext = new ReceiverContext<>((c, key) -> c.get(key));
            receiverContext.setCarrier(carrier);
            Observation parent = Observation.start("foo", () -> receiverContext, registry);
            parent.scoped(() -> {
                assertThat(MDC.getCopyOfContextMap()).containsEntry("key", "value")
                    .containsEntry("key2", "value2")
                    .containsEntry("traceId", "4bf92f3577b34da6a3ce929d0e0e4736");
                Observation child = Observation.start("bar", registry);
                child.scoped(() -> {
                    assertThat(MDC.getCopyOfContextMap()).containsEntry("key", "value")
                        .containsEntry("key2", "value2")
                        .containsEntry("traceId", "4bf92f3577b34da6a3ce929d0e0e4736");
                });
            });
        }

    }

    @Test
    void works_with_scopes_and_observations_and_ignores_baggage() {
        // Baggage
        Map<String, String> carrier = new HashMap<>();
        carrier.put("baggage", "key=value,key2=value2");
        carrier.put("traceparent", "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01");
        // carrier.put("key", "value");
        // carrier.put("key2", "value2");
        // carrier.put("b3", "00f067aa0ba902b7-00f067aa0ba902b7-1");

        // Brave with W3C
        ThreadLocalCurrentTraceContext currentTraceContext = ThreadLocalCurrentTraceContext.newBuilder()
            .addScopeDecorator(correlationScopeDecorator(mdcCorrelationScopeDecoratorBuilder()))
            .build();
        try (Tracing tracing = Tracing.newBuilder()
            .propagationFactory(micrometerTracingPropagationWithBaggage(w3cPropagationFactoryWithoutBaggage()))
            // .propagationFactory(micrometerTracingPropagationWithBaggage(b3PropagationFactory()))
            .currentTraceContext(currentTraceContext)
            .build()) {

            Tracer tracer = new BraveTracer(tracing.tracer(),
                    new BraveCurrentTraceContext(tracing.currentTraceContext()));
            BravePropagator bravePropagator = new BravePropagator(tracing);

            // Observation
            TestObservationRegistry registry = TestObservationRegistry.create();
            registry.observationConfig()
                .observationHandler(new ObservationHandler.FirstMatchingCompositeObservationHandler(
                        new PropagatingReceiverTracingObservationHandler<>(tracer, bravePropagator),
                        new DefaultTracingObservationHandler(tracer)));

            ReceiverContext<Map<String, String>> receiverContext = new ReceiverContext<>((c, key) -> c.get(key));
            receiverContext.setCarrier(carrier);
            Observation parent = Observation.start("foo", () -> receiverContext, registry);
            parent.scoped(() -> {
                assertThat(MDC.getCopyOfContextMap()).doesNotContainEntry("key", "value")
                    .doesNotContainEntry("key2", "value2")
                    .containsEntry("traceId", "4bf92f3577b34da6a3ce929d0e0e4736");
                Observation child = Observation.start("bar", registry);
                child.scoped(() -> {
                    assertThat(MDC.getCopyOfContextMap()).doesNotContainEntry("key", "value")
                        .doesNotContainEntry("key2", "value2")
                        .containsEntry("traceId", "4bf92f3577b34da6a3ce929d0e0e4736");
                });
            });
        }

    }

    private BaggagePropagation.FactoryBuilder w3cPropagationFactory(BraveBaggageManager baggageManager) {
        return BaggagePropagation.newFactoryBuilder(new W3CPropagation(baggageManager, Collections.emptyList()));
    }

    private BaggagePropagation.FactoryBuilder w3cPropagationFactoryWithoutBaggage() {
        return BaggagePropagation.newFactoryBuilder(new W3CPropagation());
    }

    private BaggagePropagation.FactoryBuilder b3PropagationFactory() {
        return BaggagePropagation.newFactoryBuilder(
                B3Propagation.newFactoryBuilder().injectFormat(B3Propagation.Format.SINGLE_NO_PARENT).build());
    }

    private Propagation.Factory micrometerTracingPropagationWithBaggage(
            BaggagePropagation.FactoryBuilder factoryBuilder) {
        List<String> remoteFields = Arrays.asList("key", "key2", "tracestate");
        for (String fieldName : remoteFields) {
            factoryBuilder.add(BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create(fieldName)));
        }
        return factoryBuilder.build();
    }

    private CorrelationScopeDecorator.Builder mdcCorrelationScopeDecoratorBuilder() {
        return MDCScopeDecorator.newBuilder();
    }

    private CurrentTraceContext.ScopeDecorator correlationScopeDecorator(CorrelationScopeDecorator.Builder builder) {
        List<String> correlationFields = Arrays.asList("key", "key2");
        for (String field : correlationFields) {
            builder.add(CorrelationScopeConfig.SingleCorrelationField.newBuilder(BaggageField.create(field))
                .flushOnUpdate()
                .build());
        }
        return builder.build();
    }

    @Test
    void extract_ignoresInvalidKeys() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        // Key contains non-printable character (ASCII 15)
        carrier.put("baggage", "valid-key=value,in\u000fvalid-key=value2");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).containsEntry("valid-key", "value")
            .hasSize(1)
            .doesNotContainKey("in\u000fvalid-key");
    }

    @Test
    void extract_ignoresKeysWithDelimitersAndSpaces() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        // Keys containing space, delimiters '?', '<', '>', '@', '/', '[', ']', '{', '}'
        carrier.put("baggage",
                "valid-key=value,key?with?question=value2,key<with<brackets=value3,key@with@at=value4,key/with/slash=value5,key[with]brackets=value6,key{with}braces=value7,key with space=value8");

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).containsEntry("valid-key", "value")
            .hasSize(1)
            .doesNotContainKey("key?with?question")
            .doesNotContainKey("key<with<brackets")
            .doesNotContainKey("key@with@at")
            .doesNotContainKey("key/with/slash")
            .doesNotContainKey("key[with]brackets")
            .doesNotContainKey("key{with}braces")
            .doesNotContainKey("key with space");
    }

    @Test
    void inject_ignoresInvalidKeys() {
        TraceContextOrSamplingFlags.Builder builder = context().toBuilder();
        BaggageField validField = BaggageField.create("valid-key");
        BaggageField invalidField = BaggageField.create("in\u000fvalid-key");
        builder.addExtra(BaggageFields.newFactory(Arrays.asList(validField, invalidField), 10).create());
        TraceContextOrSamplingFlags context = builder.build();
        validField.updateValue(context, "value");
        invalidField.updateValue(context, "value2");
        Map<String, String> carrier = new HashMap<>();

        propagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(context.context(), carrier);

        assertThat(carrier).containsEntry("baggage", "valid-key=value");
    }

    @Test
    void inject_ignoresKeysWithDelimitersAndSpaces() {
        TraceContextOrSamplingFlags.Builder builder = context().toBuilder();
        BaggageField validField = BaggageField.create("valid-key");
        BaggageField spaceField = BaggageField.create("key with space");
        BaggageField delimiterField = BaggageField.create("key?with?question");
        builder.addExtra(BaggageFields.newFactory(Arrays.asList(validField, spaceField, delimiterField), 10).create());
        TraceContextOrSamplingFlags context = builder.build();
        validField.updateValue(context, "value");
        spaceField.updateValue(context, "value2");
        delimiterField.updateValue(context, "value3");
        Map<String, String> carrier = new HashMap<>();

        propagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(context.context(), carrier);

        assertThat(carrier).containsEntry("baggage", "valid-key=value");
    }

    @Test
    void extract_limitsTo64Entries() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 70; i++) {
            sb.append("k").append(i).append("=v").append(i).append(",");
        }
        sb.setLength(sb.length() - 1);
        carrier.put("baggage", sb.toString());

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).hasSize(64);
        assertThat(baggageEntries).containsEntry("k1", "v1").containsEntry("k64", "v64").doesNotContainKey("k65");
    }

    @Test
    void inject_limitsTo64Entries() {
        // Note: Brave's BaggageFields.newFactory has a hard limit of 64
        // maxDynamicEntries.
        // We cannot create a TraceContext with more than 64 dynamic fields to test the
        // propagator's limit directly. However, we verify here that exactly 64 fields
        // are successfully injected, and the propagator's safety check
        // (MAX_BAGGAGE_ENTRIES = 64)
        // is in place as a defensive hardening measure.
        TraceContextOrSamplingFlags.Builder builder = context().toBuilder();
        List<BaggageField> fields = new ArrayList<>();
        for (int i = 1; i <= 64; i++) {
            fields.add(BaggageField.create("k" + i));
        }
        builder.addExtra(BaggageFields.newFactory(fields, 64).create());
        TraceContextOrSamplingFlags context = builder.build();
        for (int i = 1; i <= 64; i++) {
            fields.get(i - 1).updateValue(context, "v" + i);
        }
        Map<String, String> carrier = new HashMap<>();

        propagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(context.context(), carrier);

        String baggageHeader = carrier.get("baggage");
        assertThat(baggageHeader).isNotNull();
        String[] parts = baggageHeader.split(",");
        assertThat(parts).hasSize(64);
    }

    @Test
    void extract_truncatesHeaderExceeding8192Bytes() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        // Generate a very large header, e.g. 10000 bytes
        int i = 1;
        while (sb.length() < 10000) {
            sb.append("key").append(i).append("=").append("value").append(i).append(",");
            i++;
        }
        sb.setLength(sb.length() - 1);
        carrier.put("baggage", sb.toString());

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        // Should parse some entries but stop before 8192 bytes
        assertThat(baggageEntries).isNotEmpty();
        // The total number of entries should be limited, and no key from the truncated
        // part should exist
        String lastKey = "key" + (i - 1);
        assertThat(baggageEntries).doesNotContainKey(lastKey);
    }

    @Test
    void inject_limitsTo8192Bytes() {
        final int BAGGAGE_COUNT = 64;
        String longString = "value-with-long-string-to-make-it-exceed-the-limit-value-with-long-string-to-make-it-exceed-the-limit-value-with-long-string-to-make-it-exceed-the-limit-";
        TraceContextOrSamplingFlags.Builder builder = context().toBuilder();
        List<BaggageField> fields = new ArrayList<>();
        // Create enough fields to exceed 8192 bytes when serialized
        for (int i = 1; i <= BAGGAGE_COUNT; i++) {
            fields.add(BaggageField.create("key" + i));
        }
        builder.addExtra(BaggageFields.newFactory(fields, BAGGAGE_COUNT).create());
        TraceContextOrSamplingFlags context = builder.build();
        for (int i = 1; i <= BAGGAGE_COUNT; i++) {
            // Each entry is very long to exceed 8192 bytes total
            fields.get(i - 1).updateValue(context, longString + i);
        }
        assertThat(longString.length() * BAGGAGE_COUNT).isGreaterThan(8192);
        Map<String, String> carrier = new HashMap<>();

        propagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(context.context(), carrier);

        String baggageHeader = carrier.get("baggage");
        assertThat(baggageHeader).isNotNull();
        assertThat(baggageHeader.length()).isLessThanOrEqualTo(8192);
    }

    @Test
    void extract_ignoresOversizedKeyAndValue() {
        TraceContextOrSamplingFlags context = context();
        Map<String, String> carrier = new HashMap<>();

        // Key is 4097 bytes long (exceeds 4096)
        StringBuilder largeKeySb = new StringBuilder();
        for (int i = 0; i < 4097; i++) {
            largeKeySb.append("k");
        }
        String largeKey = largeKeySb.toString();

        // Value is 4097 bytes long (exceeds 4096)
        StringBuilder largeValueSb = new StringBuilder();
        for (int i = 0; i < 4097; i++) {
            largeValueSb.append("v");
        }
        String largeValue = largeValueSb.toString();

        // We test them in separate carriers to avoid triggering the 8192-byte total
        // truncation
        carrier.put("baggage", "valid-key=value,valid-key2=" + largeValue);

        TraceContextOrSamplingFlags contextWithBaggage = propagator.contextWithBaggage(carrier, context, Map::get);

        Map<String, String> baggageEntries = baggageEntries(contextWithBaggage);
        assertThat(baggageEntries).containsEntry("valid-key", "value").doesNotContainKey("valid-key2");

        // Now test large key
        Map<String, String> carrier2 = new HashMap<>();
        carrier2.put("baggage", "valid-key=value," + largeKey + "=value2");
        TraceContextOrSamplingFlags contextWithBaggage2 = propagator.contextWithBaggage(carrier2, context, Map::get);
        Map<String, String> baggageEntries2 = baggageEntries(contextWithBaggage2);
        assertThat(baggageEntries2).containsEntry("valid-key", "value").doesNotContainKey(largeKey);
    }

    @Test
    void inject_ignoresOversizedKeyAndValue() {
        TraceContextOrSamplingFlags.Builder builder = context().toBuilder();

        // Key is 4097 bytes long (exceeds 4096)
        StringBuilder largeKeySb = new StringBuilder();
        for (int i = 0; i < 4097; i++) {
            largeKeySb.append("k");
        }
        String largeKey = largeKeySb.toString();

        // Value is 4097 bytes long (exceeds 4096)
        StringBuilder largeValueSb = new StringBuilder();
        for (int i = 0; i < 4097; i++) {
            largeValueSb.append("v");
        }
        String largeValue = largeValueSb.toString();

        BaggageField validField = BaggageField.create("valid-key");
        BaggageField largeKeyField = BaggageField.create(largeKey);
        BaggageField largeValueField = BaggageField.create("valid-key2");

        builder
            .addExtra(BaggageFields.newFactory(Arrays.asList(validField, largeKeyField, largeValueField), 10).create());
        TraceContextOrSamplingFlags context = builder.build();

        validField.updateValue(context, "value");
        largeKeyField.updateValue(context, "value2");
        largeValueField.updateValue(context, largeValue);

        Map<String, String> carrier = new HashMap<>();

        propagator.injector((Propagation.Setter<Map<String, String>, String>) Map::put)
            .inject(context.context(), carrier);

        assertThat(carrier).containsEntry("baggage", "valid-key=value");
    }

}
