package org.springframework.curator.config

import java.lang.reflect.Field

import spock.lang.Specification

class CuratorNamespaceHandlerSpec extends Specification {

    CuratorNamespaceHandler handler

    def setup() {
        handler = new CuratorNamespaceHandler()
    }

    def "test the initialization of the Curator namespace handler"() {
        setup:
            Field field = handler.getClass().getSuperclass().getDeclaredField('parsers')
            field.setAccessible(true)
        when:
            handler.init()
        then:
            field.get(handler).size() == 1
            field.get(handler).client.getClass() == CuratorClientBeanDefinitionParser
    }
}
