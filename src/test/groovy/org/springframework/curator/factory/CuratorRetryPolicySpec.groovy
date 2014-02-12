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
package org.springframework.curator.factory

import spock.lang.Specification
import spock.lang.Unroll

class CuratorRetryPolicySpec extends Specification {

    def "test getting the string representation of each enumerated value"() {
        expect:
            CuratorRetryPolicy.values().each { CuratorRetryPolicy retryPolicy ->
                retryPolicy.toString() == retryPolicy.type
            }
    }

    @Unroll
    def "test finding the matching enumerated value for the retry policy type #type"() {
        expect:
            CuratorRetryPolicy.findByType(type) == expectedValue
        where:
            type														| expectedValue
            CuratorRetryPolicy.BOUNDED_EXPONENTIAL_BACKOFF.type 		| CuratorRetryPolicy.BOUNDED_EXPONENTIAL_BACKOFF
            CuratorRetryPolicy.EXPONENTIAL_BACKOFF.type 				| CuratorRetryPolicy.EXPONENTIAL_BACKOFF
            CuratorRetryPolicy.RETRY_N_TIMES.type 						| CuratorRetryPolicy.RETRY_N_TIMES
            CuratorRetryPolicy.RETRY_ONE_TIME.type 						| CuratorRetryPolicy.RETRY_ONE_TIME
            CuratorRetryPolicy.RETRY_UNTIL_ELAPSED.type 				| CuratorRetryPolicy.RETRY_UNTIL_ELAPSED
            CuratorRetryPolicy.RETRY_UNTIL_ELAPSED.type.toUpperCase()	| null
            'unknown'													| null
            ''															| null
            null														| null
    }
}
