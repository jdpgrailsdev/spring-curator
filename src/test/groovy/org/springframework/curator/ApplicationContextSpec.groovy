package org.springframework.curator

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.test.TestingServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:test-context.xml")
class ApplicationContextSpec extends Specification {

    @Shared
    TestingServer server

    @Autowired
    CuratorFramework curatorClient

    def setupSpec() {
        server = new TestingServer(1234)
    }

    def cleanupSpec() {
        server.stop()
    }

    def "test the creation of a CuratorFramework client via the Spring application context"() {
        expect:
            curatorClient != null
            curatorClient.getZookeeperClient().getCurrentConnectionString() == server.getConnectString()
    }
}
