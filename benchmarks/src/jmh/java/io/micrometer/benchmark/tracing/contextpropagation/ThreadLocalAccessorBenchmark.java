package io.micrometer.benchmark.tracing.contextpropagation;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor;
import io.micrometer.tracing.test.simple.SimpleTracer;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(1)
public class ThreadLocalAccessorBenchmark {

    private ObservationAwareSpanThreadLocalAccessor accessor;
    private Span mockSpan;

    @Setup
    public void setup() {
        Tracer tracer = new SimpleTracer();
        accessor = new ObservationAwareSpanThreadLocalAccessor(ObservationRegistry.create(), tracer);
        mockSpan = tracer.nextSpan();
    }

    @Benchmark
    public void setValueAndRestore() {
        accessor.setValue(mockSpan);
        accessor.restore(mockSpan);
    }
}
