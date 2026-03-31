package io.micrometer.benchmark.tracing.handler;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.handler.TracingObservationHandler;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(1)
public class TracingContextBenchmark {

    private CurrentTraceContext.Scope mockScope;

    @Setup
    public void setup() {
        mockScope = () -> {};
    }

    @Benchmark
    public TracingObservationHandler.TracingContext createAndSetScope() {
        TracingObservationHandler.TracingContext context = new TracingObservationHandler.TracingContext();
        context.setScope(mockScope);
        return context;
    }

    @Benchmark
    public TracingObservationHandler.TracingContext createSetAndClearScope() {
        TracingObservationHandler.TracingContext context = new TracingObservationHandler.TracingContext();
        context.setScope(mockScope);
        context.setScope(null);
        return context;
    }
}
