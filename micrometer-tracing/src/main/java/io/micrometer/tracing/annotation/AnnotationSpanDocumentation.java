/*
 * Copyright © 2023 Broadcom Inc. and/or its subsidiaries. All Rights Reserved.
 * Copyright 2023 VMware, Inc.
 */
package io.micrometer.tracing.annotation;

import io.micrometer.common.docs.KeyName;
import io.micrometer.tracing.docs.EventValue;
import io.micrometer.tracing.docs.SpanDocumentation;

enum AnnotationSpanDocumentation implements SpanDocumentation {

    /**
     * Span that wraps a @NewSpan or @ContinueSpan annotations.
     */
    ANNOTATION_NEW_OR_CONTINUE_SPAN {
        @Override
        public String getName() {
            return "%s";
        }

        @Override
        public KeyName[] getKeyNames() {
            return Tags.values();
        }

        @Override
        public EventValue[] getEvents() {
            return Events.values();
        }

    };

    /**
     * Tags related to Micrometer Tracing annotations.
     *
     * @author Marcin Grzejszczak
     */
    enum Tags implements KeyName {

        /**
         * Class name where a method got annotated with a Micrometer Tracing annotation.
         */
        CLASS {
            @Override
            public String asString() {
                return "annotated.class";
            }
        },

        /**
         * Method name that got annotated with Micrometer Tracing annotation.
         */
        METHOD {
            @Override
            public String asString() {
                return "annotated.method";
            }
        }

    }

    enum Events implements EventValue {

        /**
         * Annotated before executing a method annotated with @ContinueSpan or @NewSpan.
         */
        BEFORE {
            @Override
            public String getValue() {
                return "%s.before";
            }
        },

        /**
         * Annotated after executing a method annotated with @ContinueSpan or @NewSpan.
         */
        AFTER {
            @Override
            public String getValue() {
                return "%s.after";
            }
        },

        /**
         * Annotated after throwing an exception from a method annotated
         * with @ContinueSpan or @NewSpan.
         */
        AFTER_FAILURE {
            @Override
            public String getValue() {
                return "%s.afterFailure";
            }
        }

    }

}
