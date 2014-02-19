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
package org.springframework.curator.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.curator.factory.CuratorFrameworkFactoryBean;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Handles the parsing of the Apache Curator client configuration element from
 * the Spring application context.
 *
 * @author Jonathan Pearlin
 * @since 1.0.0
 */
public class CuratorClientBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(final Element element, final ParserContext parserContext) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(CuratorFrameworkFactoryBean.class);
        setConfiguration(builder, element);
        return getBeanDefinition(builder, element, parserContext);
    }

    private void setConfiguration(final BeanDefinitionBuilder builder, final Element element) {
        if(StringUtils.hasText(element.getAttribute("acl-provider-ref"))) {
            builder.addPropertyReference("aclProvider", element.getAttribute("acl-provider-ref"));
        }

        if(StringUtils.hasText(element.getAttribute("compression-provider-ref"))) {
            builder.addPropertyReference("compressionProvider", element.getAttribute("compression-provider-ref"));
        }

        if(StringUtils.hasText(element.getAttribute("ensemble-provider-ref"))) {
            builder.addPropertyReference("ensembleProvider", element.getAttribute("ensemble-provider-ref"));
        }

        if(StringUtils.hasText(element.getAttribute("thread-factory-ref"))) {
            builder.addPropertyReference("threadFactory", element.getAttribute("thread-factory-ref"));
        }

        if(StringUtils.hasText(element.getAttribute("zookeeper-factory-ref"))) {
            builder.addPropertyReference("zookeeperFactory", element.getAttribute("zookeeper-factory-ref"));
        }

        builder.addPropertyValue("canBeReadOnly", Boolean.valueOf(element.getAttribute("read-only")));
        builder.addPropertyValue("connectionString", element.getAttribute("connection-string"));
        builder.addPropertyValue("connectionTimeout", getSafeInteger(element.getAttribute("connection-timeout")));
        builder.addPropertyValue("defaultData", element.getAttribute("default-data"));
        builder.addPropertyValue("namespace", element.getAttribute("namespace"));
        builder.addPropertyValue("sessionTimeout", getSafeInteger(element.getAttribute("session-timeout")));
        addAuthorization(builder, element);
        addRetryPolicy(builder, element);
    }

    private AbstractBeanDefinition getBeanDefinition(final BeanDefinitionBuilder builder, final Element source, final ParserContext context) {
        final AbstractBeanDefinition definition = builder.getBeanDefinition();
        definition.setSource(context.extractSource(source));
        return definition;
    }

    private void addAuthorization(final BeanDefinitionBuilder builder, final Element element) {
	    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            if (element.getChildNodes().item(i) instanceof Element) {
                final Element potentialAuthorizationElement = (Element) element.getChildNodes().item(i);

                if (potentialAuthorizationElement.getNodeName().endsWith("authorization")) {
                    builder.addPropertyValue("auth", potentialAuthorizationElement.getAttribute("credentials"));
                    builder.addPropertyValue("scheme", potentialAuthorizationElement.getAttribute("scheme"));
                 }
             }
        }
    }

    private void addRetryPolicy(final BeanDefinitionBuilder builder, final Element element) {
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            if (element.getChildNodes().item(i) instanceof Element) {
                final Element potentialRetryPolicyElement = (Element) element.getChildNodes().item(i);

                if (potentialRetryPolicyElement.getNodeName().endsWith("retry-policy")) {
                    for(int j=0; j<potentialRetryPolicyElement.getChildNodes().getLength(); j++) {
                        if (potentialRetryPolicyElement.getChildNodes().item(j) instanceof Element) {
                            final Element retryPolicyChildElement = (Element) potentialRetryPolicyElement.getChildNodes().item(j);

                            builder.addPropertyValue("retryPolicyType", normalizeName(retryPolicyChildElement.getNodeName()));

                            if(StringUtils.hasText(retryPolicyChildElement.getAttribute("max-retries"))) {
                                builder.addPropertyValue("retryPolicyMaxRetries", Integer.parseInt(retryPolicyChildElement.getAttribute("max-retries")));
                            }

                            if(StringUtils.hasText(retryPolicyChildElement.getAttribute("base-sleep-time"))) {
                                builder.addPropertyValue("retryPolicyBaseSleepTime", Integer.parseInt(retryPolicyChildElement.getAttribute("base-sleep-time")));
                            }

                            if(StringUtils.hasText(retryPolicyChildElement.getAttribute("max-sleep-time"))) {
                                builder.addPropertyValue("retryPolicyMaxSleepTime", Integer.parseInt(retryPolicyChildElement.getAttribute("max-sleep-time")));
                            }

                            if(StringUtils.hasText(retryPolicyChildElement.getAttribute("sleep-between-retries"))) {
                                builder.addPropertyValue("retryPolicySleepBetweenRetries", Integer.parseInt(retryPolicyChildElement.getAttribute("sleep-between-retries")));
                            }

                            if(StringUtils.hasText(retryPolicyChildElement.getAttribute("max-elapsed-time"))) {
                                builder.addPropertyValue("retryPolicyMaxElapsedTime", Integer.parseInt(retryPolicyChildElement.getAttribute("max-elapsed-time")));
                            }
                        }
                    }
                }
            }
        }
    }

    private Integer getSafeInteger(final String value) {
        if(StringUtils.hasText(value)) {
            return Integer.valueOf(value);
        }

        return null;
    }

    private String normalizeName(final String nodeName) {
        if(StringUtils.hasText(nodeName)) {
            if(nodeName.indexOf(':') != -1) {
	            int index = nodeName.indexOf(':');
                return nodeName.substring(index+1);
            }
        }

        return nodeName;
    }
}