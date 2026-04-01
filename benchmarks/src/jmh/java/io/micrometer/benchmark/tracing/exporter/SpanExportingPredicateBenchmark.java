package io.micrometer.benchmark.tracing.exporter;

import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.exporter.SpanIgnoringSpanExportingPredicate;
import io.micrometer.tracing.Span;
import org.openjdk.jmh.annotations.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(1)
public class SpanExportingPredicateBenchmark {

    private SpanIgnoringSpanExportingPredicate predicate;
    private FinishedSpan testSpan;

    @Setup
    public void setup() {
        predicate = new SpanIgnoringSpanExportingPredicate(
                Arrays.asList("ignore1", "ignore2"),
                Collections.singletonList("ignore3")
        );
        testSpan = new DummyFinishedSpan("test-span");
    }

    @Benchmark
    public boolean isExportable() {
        return predicate.isExportable(testSpan);
    }

    static class DummyFinishedSpan implements FinishedSpan {
        private String name;

        DummyFinishedSpan(String name) {
            this.name = name;
        }

        @Override public FinishedSpan setName(String name) { this.name = name; return this; }
        @Override public String getName() { return name; }
        @Override public Instant getStartTimestamp() { return null; }
        @Override public Instant getEndTimestamp() { return null; }
        @Override public FinishedSpan setTags(Map<String, String> tags) { return this; }
        @Override public Map<String, String> getTags() { return null; }
        @Override public FinishedSpan setEvents(Collection<Map.Entry<Long, String>> events) { return this; }
        @Override public Collection<Map.Entry<Long, String>> getEvents() { return null; }
        @Override public String getSpanId() { return null; }
        @Override public String getParentId() { return null; }
        @Override public String getRemoteIp() { return null; }
        @Override public FinishedSpan setLocalIp(String ip) { return this; }
        @Override public int getRemotePort() { return 0; }
        @Override public FinishedSpan setRemotePort(int port) { return this; }
        @Override public String getTraceId() { return null; }
        @Override public Throwable getError() { return null; }
        @Override public FinishedSpan setError(Throwable error) { return this; }
        @Override public Span.Kind getKind() { return null; }
        @Override public String getRemoteServiceName() { return null; }
        @Override public FinishedSpan setRemoteServiceName(String remoteServiceName) { return this; }
    }
}
