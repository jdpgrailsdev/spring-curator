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

import java.util.concurrent.ThreadFactory

import org.apache.curator.RetryPolicy
import org.apache.curator.ensemble.EnsembleProvider
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.api.ACLProvider
import org.apache.curator.framework.api.CompressionProvider
import org.apache.curator.framework.imps.CuratorFrameworkState
import org.apache.curator.retry.BoundedExponentialBackoffRetry
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.retry.RetryOneTime
import org.apache.curator.retry.RetryUntilElapsed
import org.apache.curator.test.TestingServer
import org.apache.curator.utils.ZookeeperFactory

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class CuratorFrameworkFactoryBeanSpec extends Specification {

    @Shared
    TestingServer server

    @Shared
    String connectionString

    CuratorFrameworkFactoryBean factory

    def setup() {
        factory = new CuratorFrameworkFactoryBean()
    }

    def setupSpec() {
        server = new TestingServer()
        connectionString = server.getConnectString()
    }

    def cleanupSpec() {
        server.stop()
    }

    def "test the object type returned by the factory"() {
        expect:
            factory.getObjectType() == CuratorFramework
    }

    def "test the building and validation of the client"() {
        setup:
            factory.connectionString = connectionString
            factory.retryPolicyType = CuratorRetryPolicy.RETRY_ONE_TIME.toString()
            factory.retryPolicySleepBetweenRetries = 0
        when:
            factory.afterPropertiesSet()
        then:
            notThrown IllegalArgumentException
            factory.client != null
            factory.client.getState() == CuratorFrameworkState.STARTED
        cleanup:
            factory.destroy()
    }

    def "test attempting to destroy the client when the client is null"() {
        when:
            factory.destroy()
        then:
            notThrown Exception
    }

    def "test attemptiong to validate the client when the client is null"() {
        when:
            factory.validateClient()
        then:
            thrown IllegalStateException
    }

    def "test attempting to validate the client when the client is invalid"() {
        setup:
            factory.connectionString = 'localhost'
            factory.retryPolicyType = CuratorRetryPolicy.RETRY_ONE_TIME.toString()
            factory.retryPolicySleepBetweenRetries = 0
        when:
            factory.validateClient()
        then:
            thrown IllegalStateException
    }

    def "test building the Curator client from the configuration"() {
        setup:
            factory.aclProvider = Mock(ACLProvider)
            factory.auth = 'user:pass'.getBytes()
            factory.canBeReadOnly = false
            factory.compressionProvider = Mock(CompressionProvider)
            factory.connectionString = connectionString
            factory.connectionTimeout = 1000
            factory.defaultData = '{}'.getBytes()
            factory.ensembleProvider = null
            factory.namespace = 'test'
            factory.retryPolicyType = CuratorRetryPolicy.RETRY_ONE_TIME.toString()
            factory.retryPolicySleepBetweenRetries = 0
            factory.scheme = 'basic'
            factory.sessionTimeout = 1000
            factory.threadFactory = Mock(ThreadFactory)
            factory.zookeeperFactory = Mock(ZookeeperFactory)
        when:
            factory.buildClient()
        then:
            factory.client != null
    }

    def "test building the Curator client from the configuration without a retry policy"() {
        setup:
            factory.aclProvider = Mock(ACLProvider)
            factory.auth = 'user:pass'.getBytes()
            factory.canBeReadOnly = false
            factory.compressionProvider = Mock(CompressionProvider)
            factory.connectionString = connectionString
            factory.connectionTimeout = 1000
            factory.defaultData = '{}'.getBytes()
            factory.ensembleProvider = null
            factory.namespace = 'test'
            factory.scheme = 'basic'
            factory.sessionTimeout = 1000
            factory.threadFactory = Mock(ThreadFactory)
            factory.zookeeperFactory = Mock(ZookeeperFactory)
        when:
            factory.buildClient()
        then:
            thrown IllegalArgumentException
    }

    def "test building the Curator client from the configuration with a connection string"() {
        setup:
            factory.connectionString = connectionString
            factory.retryPolicyType = CuratorRetryPolicy.RETRY_ONE_TIME.toString()
            factory.retryPolicySleepBetweenRetries = 0
        when:
            factory.buildClient()
        then:
            factory.client != null
    }

    def "test building the Curator client from the configuration with an ensemble provider"() {
        setup:
            factory.ensembleProvider = Mock(EnsembleProvider)
            factory.retryPolicyType = CuratorRetryPolicy.RETRY_ONE_TIME.toString()
            factory.retryPolicySleepBetweenRetries = 0
        when:
            factory.buildClient()
        then:
            factory.client != null
    }

    def "test building the Curator client from the configuration with a connection string and an ensemble provider"() {
        setup:
            factory.connectionString = connectionString
            factory.ensembleProvider = Mock(EnsembleProvider)
        when:
            factory.buildClient()
        then:
            thrown IllegalArgumentException
    }

    def "test building the Curator client from the configuration without either a connection string and an ensemble provider"() {
        when:
            factory.buildClient()
        then:
            thrown IllegalArgumentException
    }

    @Unroll
    def "test creating a retry policy object for requested type #type"() {
        setup:
            factory.retryPolicyType = type
            factory.retryPolicyBaseSleepTime = 1000
            factory.retryPolicyMaxElapsedTime = 1000
            factory.retryPolicyMaxRetries = 5
            factory.retryPolicyMaxSleepTime = 1000
            factory.retryPolicySleepBetweenRetries = 1000
        when:
            RetryPolicy retryPolicy = factory.createRetryPolicy()
        then:
            retryPolicy != null
            retryPolicy.getClass() == expectedType
        where:
            type														| expectedType
            CuratorRetryPolicy.BOUNDED_EXPONENTIAL_BACKOFF.toString()	| BoundedExponentialBackoffRetry
            CuratorRetryPolicy.EXPONENTIAL_BACKOFF.toString()			| ExponentialBackoffRetry
            CuratorRetryPolicy.RETRY_N_TIMES.toString()					| RetryNTimes
            CuratorRetryPolicy.RETRY_ONE_TIME.toString()				| RetryOneTime
            CuratorRetryPolicy.RETRY_UNTIL_ELAPSED.toString()			| RetryUntilElapsed
    }

    def "test the creation of the retry policy from an unknown type"() {
        setup:
            factory.retryPolicyType = 'unknown'
        when:
            factory.createRetryPolicy()
        then:
            thrown IllegalArgumentException
    }

    @Unroll
    def "test setting the auth data with value #auth"() {
        when:
            factory.setAuth(auth)
        then:
            factory.getAuth() == expectedAuth
        where:
            auth				| expectedAuth
            'value'				| 'value'.getBytes()
            ''					| null
            null				| null
    }

    @Unroll
    def "test setting the default data with value #defaultData"() {
        when:
            factory.setDefaultData(defaultData)
        then:
            factory.getDefaultData() == expectedDefaultData
        where:
            defaultData				| expectedDefaultData
            '{}'					| '{}'.getBytes()
            ''						| null
            null					| null
    }
}
