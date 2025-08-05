/**
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 the original author or authors.
 */
package brave.baggage;

import java.util.ArrayDeque;

public class CorrelationFlushScopeArrayReader {

    public static ArrayDeque<Object> readForCurrentThread() {
        return CorrelationFlushScope.updateScopeStack.get();
    }

    public static int size() {
        ArrayDeque<Object> objects = readForCurrentThread();
        if (objects != null) {
            return objects.size();
        }
        return 0;
    }

}
