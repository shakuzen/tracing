/**
 * Copyright © 2022 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2022 the original author or authors.
 */
package io.micrometer.tracing.test.reporter.wavefront;

import io.micrometer.tracing.reporter.wavefront.WavefrontSpanHandler;
import org.mockito.BDDMockito;

public class WavefrontAccessor {

    /**
     * @return mocked span handler for Brave
     */
    public static WavefrontSpanHandler setMockForBrave() {
        WavefrontSpanHandler handler = BDDMockito.mock(WavefrontSpanHandler.class);
        WavefrontBraveSetup.mockHandler = handler;
        return handler;
    }

    /**
     * @return mocked span handler for Otel
     */
    public static WavefrontSpanHandler setMockForOTel() {
        WavefrontSpanHandler handler = BDDMockito.mock(WavefrontSpanHandler.class);
        WavefrontOtelSetup.mockHandler = handler;
        return handler;
    }

    /**
     * Resets any mocks.
     */
    public static void resetMocks() {
        WavefrontBraveSetup.mockHandler = null;
        WavefrontOtelSetup.mockHandler = null;
    }

}
