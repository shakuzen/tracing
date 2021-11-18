/*
 * Copyright 2013-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micrometer.tracing.test;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.test.reporter.zipkin.ZipkinBraveSetup;
import io.micrometer.tracing.test.reporter.zipkin.ZipkinOtelSetup;
import io.micrometer.tracing.util.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import zipkin2.CheckResult;

public abstract class SampleTestRunner {
    private final SamplerRunnerConfig samplerRunnerConfig;

    protected final MeterRegistry meterRegistry;

    public SampleTestRunner(SamplerRunnerConfig samplerRunnerConfig, MeterRegistry meterRegistry) {
        this.samplerRunnerConfig = samplerRunnerConfig;
        this.meterRegistry = meterRegistry;
    }

    @ParameterizedTest
    @EnumSource(TracingSetup.class)
    public void run(TracingSetup tracingSetup) {
        tracingSetup.run(meterRegistry, yourCode());
        tracingSetup.printTracingLink();
    }

    @AfterEach
    void printMetrics() {
        this.meterRegistry.close();
    }

    public abstract Runnable yourCode();

    enum TracingSetup {
        ZIPKIN_OTEL {
            @Override
            void run(SamplerRunnerConfig samplerRunnerConfig, MeterRegistry meterRegistry, Runnable runnable) {
                ZipkinOtelSetup setup = ZipkinOtelSetup.builder().register(meterRegistry);
                CheckResult checkResult = setup.getBuildingBlocks().sender.check();
                Assumptions.assumeTrue(checkResult.ok(), "There was a problem [" + checkResult.error().toString() + "] with connecting to Zipkin. Will NOT run any tests");
                ZipkinOtelSetup.run(setup, __ -> runnable.run());
            }
        },
        ZIPKIN_BRAVE {
            @Override
            void run(SamplerRunnerConfig samplerRunnerConfig, MeterRegistry meterRegistry, Runnable runnable) {
                ZipkinBraveSetup setup = ZipkinBraveSetup.builder().register(meterRegistry);
                CheckResult checkResult = setup.getBuildingBlocks().sender.check();
                Assumptions.assumeTrue(checkResult.ok(), "There was a problem [" + checkResult.error().toString() + "] with connecting to Zipkin. Will NOT run any tests");
                ZipkinBraveSetup.run(setup, __ -> runnable.run());
            }
        },
        WAVEFRONT_OTEL {
            @Override
            void run(SamplerRunnerConfig samplerRunnerConfig, MeterRegistry meterRegistry, Runnable runnable) {
                checkWavefrontAssumptions(samplerRunnerConfig);
                Wa
            }
        },
        WAVEFRONT_BRAVE {
            @Override
            void run(SamplerRunnerConfig samplerRunnerConfig, MeterRegistry meterRegistry, Runnable runnable) {
                checkWavefrontAssumptions(samplerRunnerConfig);
            }
        }

        private static void checkZipkinAssumptions(SamplerRunnerConfig samplerRunnerConfig) {
            Assumptions.assumeTrue(StringUtils.isNotBlank(samplerRunnerConfig.serverUrl), "To run tests against Tanzu Observability by Wavefront you need to set the Wavefront server url");
            Assumptions.assumeTrue(StringUtils.isNotBlank(samplerRunnerConfig.wavefrontToken), "To run tests against Tanzu Observability by Wavefront you need to set the Wavefront token");
        }

        private static void checkWavefrontAssumptions(SamplerRunnerConfig samplerRunnerConfig) {
            Assumptions.assumeTrue(StringUtils.isNotBlank(samplerRunnerConfig.serverUrl), "To run tests against Tanzu Observability by Wavefront you need to set the Wavefront server url");
            Assumptions.assumeTrue(StringUtils.isNotBlank(samplerRunnerConfig.wavefrontToken), "To run tests against Tanzu Observability by Wavefront you need to set the Wavefront token");
        }

        abstract void run(SamplerRunnerConfig samplerRunnerConfig, MeterRegistry meterRegistry, Runnable runnable);

        abstract void printTracingLink();
    }

    public static class SamplerRunnerConfig {
        public String wavefrontToken;
        public String serverUrl;
        public String zipkinUrl = "http://localhost:9411";

        public SamplerRunnerConfig(String wavefrontToken, String serverUrl, String zipkinUrl) {
            this.wavefrontToken = wavefrontToken;
            this.serverUrl = serverUrl;
            this.zipkinUrl = zipkinUrl;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String wavefrontToken;

            private String serverToken;

            private String zipkinUrl;

            public Builder wavefrontToken(String wavefrontToken) {
                this.wavefrontToken = wavefrontToken;
                return this;
            }

            public Builder serverToken(String serverToken) {
                this.serverToken = serverToken;
                return this;
            }

            public Builder zipkinUrl(String zipkinUrl) {
                this.zipkinUrl = zipkinUrl;
                return this;
            }

            public SampleTestRunner.SamplerRunnerConfig createSamplerRunnerConfig() {
                return new SampleTestRunner.SamplerRunnerConfig(this.wavefrontToken, this.serverToken, this.zipkinUrl);
            }
        }
    }
}
