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
