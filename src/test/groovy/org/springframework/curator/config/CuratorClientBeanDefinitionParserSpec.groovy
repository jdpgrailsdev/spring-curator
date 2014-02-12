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
package org.springframework.curator.config

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.springframework.beans.factory.parsing.ProblemReporter
import org.springframework.beans.factory.parsing.ReaderEventListener
import org.springframework.beans.factory.parsing.SourceExtractor
import org.springframework.beans.factory.support.AbstractBeanDefinition
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate
import org.springframework.beans.factory.xml.NamespaceHandlerResolver
import org.springframework.beans.factory.xml.ParserContext
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.beans.factory.xml.XmlReaderContext
import org.springframework.core.io.Resource
import org.w3c.dom.Document
import org.w3c.dom.Element

import spock.lang.Specification

class CuratorClientBeanDefinitionParserSpec extends Specification {

    CuratorClientBeanDefinitionParser parser

    def setup() {
        parser = new CuratorClientBeanDefinitionParser()
    }

    def "test parsing a well-formed XML configuration of a Curator client with a bounded exponential backoff retry policy"() {
        setup:
            Element element = parseXml('''<client id="test-client" connection-string="localhost" read-only="true" connection-timeout="50" session-timeout="50" namespace="test">
                <authorization scheme="test" credentials="user:pass" />
                <retry-policy>
                    <bounded-exponential-backoff max-retries="1" base-sleep-time="1000" max-sleep-time="1000"/>
                </retry-policy>
            </client>''')
            Resource resource = Mock(Resource)
            ProblemReporter problemReporter = Mock(ProblemReporter)
            ReaderEventListener eventListener = Mock(ReaderEventListener)
            SourceExtractor sourceExtractor = Mock(SourceExtractor)
            XmlBeanDefinitionReader reader = Mock(XmlBeanDefinitionReader)
            NamespaceHandlerResolver namespaceHandlerResolver = Mock(NamespaceHandlerResolver)
            XmlReaderContext context = new XmlReaderContext(resource, problemReporter, eventListener, sourceExtractor, reader, namespaceHandlerResolver)
            BeanDefinitionParserDelegate delegate = Mock(BeanDefinitionParserDelegate)
            ParserContext parserContext = new ParserContext(context, delegate)
        when:
            AbstractBeanDefinition definition = parser.parseInternal(element, parserContext)
        then:
            definition != null
            definition.getPropertyValues().size() == 12
    }

    def "test parsing a well-formed XML configuration of a Curator client with a exponential backoff retry policy"() {
        setup:
            Element element = parseXml('''<client id="test-client" connection-string="localhost" read-only="true" connection-timeout="50" session-timeout="50" namespace="test">
                <authorization scheme="test" credentials="user:pass" />
                <retry-policy>
                    <exponential-backoff max-retries="1" base-sleep-time="1000" max-sleep-time="1000"/>
                </retry-policy>
            </client>''')
            Resource resource = Mock(Resource)
            ProblemReporter problemReporter = Mock(ProblemReporter)
            ReaderEventListener eventListener = Mock(ReaderEventListener)
            SourceExtractor sourceExtractor = Mock(SourceExtractor)
            XmlBeanDefinitionReader reader = Mock(XmlBeanDefinitionReader)
            NamespaceHandlerResolver namespaceHandlerResolver = Mock(NamespaceHandlerResolver)
            XmlReaderContext context = new XmlReaderContext(resource, problemReporter, eventListener, sourceExtractor, reader, namespaceHandlerResolver)
            BeanDefinitionParserDelegate delegate = Mock(BeanDefinitionParserDelegate)
            ParserContext parserContext = new ParserContext(context, delegate)
        when:
            AbstractBeanDefinition definition = parser.parseInternal(element, parserContext)
        then:
            definition != null
            definition.getPropertyValues().size() == 12
    }

    def "test parsing a well-formed XML configuration of a Curator client with a retry n times retry policy"() {
        setup:
            Element element = parseXml('''<client id="test-client" connection-string="localhost" read-only="true" connection-timeout="50" session-timeout="50" namespace="test">
                <authorization scheme="test" credentials="user:pass" />
                <retry-policy>
                    <retry-n-times max-retries="1" sleep-between-retries="1000" />
                </retry-policy>
            </client>''')
            Resource resource = Mock(Resource)
            ProblemReporter problemReporter = Mock(ProblemReporter)
            ReaderEventListener eventListener = Mock(ReaderEventListener)
            SourceExtractor sourceExtractor = Mock(SourceExtractor)
            XmlBeanDefinitionReader reader = Mock(XmlBeanDefinitionReader)
            NamespaceHandlerResolver namespaceHandlerResolver = Mock(NamespaceHandlerResolver)
            XmlReaderContext context = new XmlReaderContext(resource, problemReporter, eventListener, sourceExtractor, reader, namespaceHandlerResolver)
            BeanDefinitionParserDelegate delegate = Mock(BeanDefinitionParserDelegate)
            ParserContext parserContext = new ParserContext(context, delegate)
        when:
            AbstractBeanDefinition definition = parser.parseInternal(element, parserContext)
        then:
            definition != null
            definition.getPropertyValues().size() == 11
    }

    def "test parsing a well-formed XML configuration of a Curator client with a retry one time retry policy"() {
        setup:
            Element element = parseXml('''<client id="test-client" connection-string="localhost" read-only="true" connection-timeout="50" session-timeout="50" namespace="test">
                <authorization scheme="test" credentials="user:pass" />
                <retry-policy>
                    <retry-one-time sleep-between-retries="1000" />
                </retry-policy>
            </client>''')
            Resource resource = Mock(Resource)
            ProblemReporter problemReporter = Mock(ProblemReporter)
            ReaderEventListener eventListener = Mock(ReaderEventListener)
            SourceExtractor sourceExtractor = Mock(SourceExtractor)
            XmlBeanDefinitionReader reader = Mock(XmlBeanDefinitionReader)
            NamespaceHandlerResolver namespaceHandlerResolver = Mock(NamespaceHandlerResolver)
            XmlReaderContext context = new XmlReaderContext(resource, problemReporter, eventListener, sourceExtractor, reader, namespaceHandlerResolver)
            BeanDefinitionParserDelegate delegate = Mock(BeanDefinitionParserDelegate)
            ParserContext parserContext = new ParserContext(context, delegate)
        when:
            AbstractBeanDefinition definition = parser.parseInternal(element, parserContext)
        then:
            definition != null
            definition.getPropertyValues().size() == 10
    }

    def "test parsing a well-formed XML configuration of a Curator client with a retry until elapsed retry policy"() {
        setup:
            Element element = parseXml('''<client id="test-client" connection-string="localhost" read-only="true" connection-timeout="50" session-timeout="50" namespace="test">
                <authorization scheme="test" credentials="user:pass" />
                <retry-policy>
                    <retry-until-elapsed max-elapsed-time="1000" sleep-between-retries="1000" />
                </retry-policy>
            </client>''')
            Resource resource = Mock(Resource)
            ProblemReporter problemReporter = Mock(ProblemReporter)
            ReaderEventListener eventListener = Mock(ReaderEventListener)
            SourceExtractor sourceExtractor = Mock(SourceExtractor)
            XmlBeanDefinitionReader reader = Mock(XmlBeanDefinitionReader)
            NamespaceHandlerResolver namespaceHandlerResolver = Mock(NamespaceHandlerResolver)
            XmlReaderContext context = new XmlReaderContext(resource, problemReporter, eventListener, sourceExtractor, reader, namespaceHandlerResolver)
            BeanDefinitionParserDelegate delegate = Mock(BeanDefinitionParserDelegate)
            ParserContext parserContext = new ParserContext(context, delegate)
        when:
            AbstractBeanDefinition definition = parser.parseInternal(element, parserContext)
        then:
            definition != null
            definition.getPropertyValues().size() == 11
    }

    def "test parsing a well-formed XML configuration of a Curator client without authorization details"() {
        setup:
            Element element = parseXml('''<client id="test-client" connection-string="localhost" read-only="true" connection-timeout="50" session-timeout="50" namespace="test">
                <retry-policy>
                    <retry-one-time sleep-between-retries="1000" />
                </retry-policy>
            </client>''')
            Resource resource = Mock(Resource)
            ProblemReporter problemReporter = Mock(ProblemReporter)
            ReaderEventListener eventListener = Mock(ReaderEventListener)
            SourceExtractor sourceExtractor = Mock(SourceExtractor)
            XmlBeanDefinitionReader reader = Mock(XmlBeanDefinitionReader)
            NamespaceHandlerResolver namespaceHandlerResolver = Mock(NamespaceHandlerResolver)
            XmlReaderContext context = new XmlReaderContext(resource, problemReporter, eventListener, sourceExtractor, reader, namespaceHandlerResolver)
            BeanDefinitionParserDelegate delegate = Mock(BeanDefinitionParserDelegate)
            ParserContext parserContext = new ParserContext(context, delegate)
        when:
            AbstractBeanDefinition definition = parser.parseInternal(element, parserContext)
        then:
            definition != null
            definition.getPropertyValues().size() == 8
    }

    def "test parsing a well-formed XML configuration of a Curator client without a retry policy"() {
        setup:
            Element element = parseXml('''<client id="test-client" connection-string="localhost" read-only="true" connection-timeout="50" session-timeout="50" namespace="test">
                <authorization scheme="test" credentials="user:pass" />
            </client>''')
            Resource resource = Mock(Resource)
            ProblemReporter problemReporter = Mock(ProblemReporter)
            ReaderEventListener eventListener = Mock(ReaderEventListener)
            SourceExtractor sourceExtractor = Mock(SourceExtractor)
            XmlBeanDefinitionReader reader = Mock(XmlBeanDefinitionReader)
            NamespaceHandlerResolver namespaceHandlerResolver = Mock(NamespaceHandlerResolver)
            XmlReaderContext context = new XmlReaderContext(resource, problemReporter, eventListener, sourceExtractor, reader, namespaceHandlerResolver)
            BeanDefinitionParserDelegate delegate = Mock(BeanDefinitionParserDelegate)
            ParserContext parserContext = new ParserContext(context, delegate)
        when:
            AbstractBeanDefinition definition = parser.parseInternal(element, parserContext)
        then:
            definition != null
            definition.getPropertyValues().size() == 8
    }

    protected Element parseXml(xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        DocumentBuilder builder = factory.newDocumentBuilder()
        Document document = builder.parse(new ByteArrayInputStream(xml.getBytes()))
        document.getDocumentElement()
    }
}
