/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.curator.factory;

/**
 * Enumeration that encapsulates the different retry policies currently provided
 * by the core Apache Curator framework.
 *
 * @author Jonathan Pearlin
 * @since 1.0.0
 */
public enum CuratorRetryPolicy {

    BOUNDED_EXPONENTIAL_BACKOFF("bounded-exponential-backoff"),
    EXPONENTIAL_BACKOFF("exponential-backoff"),
    RETRY_N_TIMES("retry-n-times"),
    RETRY_ONE_TIME("retry-one-time"),
    RETRY_UNTIL_ELAPSED("retry-until-elapsed");

    /**
     * The type of the retry policy, as defined in the XML schema.
     */
    private final String type;

    /**
     * Constructs a new enumerated value for the given type.
     * @param type The retry policy type.
     */
    private CuratorRetryPolicy(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    /**
     * Finds the matching {@link CuratorRetryPolicy} enumerated value
     * that matches the provided type string.
     * @param type The retry policy type.
     * @return The matching {@link CuratorRetryPolicy} enumerated value
     * 	for the given type or {@code null} if no match is found.
     */
    public static CuratorRetryPolicy findByType(final String type) {
        CuratorRetryPolicy retryPolicy = null;

        for(final CuratorRetryPolicy currentRetryPolicy : values()) {
            if(currentRetryPolicy.type.equals(type)) {
                retryPolicy = currentRetryPolicy;
                break;
            }
        }

        return retryPolicy;
    }
}
