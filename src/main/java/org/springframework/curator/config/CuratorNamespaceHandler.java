package org.springframework.curator.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Support class that registers the custom Spring Curator parsers
 * with the appropriate XML elements.
 *
 * @author Jonathan Pearlin
 * @since 1.0.0
 */
public class CuratorNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("client", new CuratorClientBeanDefinitionParser());
    }
}
