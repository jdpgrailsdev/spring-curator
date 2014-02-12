package org.springframework.curator.factory;

import java.util.concurrent.ThreadFactory;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.curator.utils.ZookeeperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * {@link org.springframework.beans.factory.FactoryBean} that creates a
 * new Apache Curator framework client that can be used to access an
 * Apache ZooKeeper ensemble/instance.
 *
 * <p>This factory creates a Singleton instance of the Curator client.  Per
 * the Apache Curator documentation:
 * <br/>
 * <blockquote cite="http://curator.apache.org/getting-started.html">"You only need one CuratorFramework object for each ZooKeeper cluster you are connecting to."</blockquote>
 *
 * <p>Additionally, the factory will handle the closing of the {@link CuratorFramework}
 * client instance upon bean destroy
 *
 * @author Jonathan Pearlin
 * @since 1.0.0
 */
public class CuratorFrameworkFactoryBean implements FactoryBean<CuratorFramework>, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(CuratorFrameworkFactoryBean.class);

    private ACLProvider aclProvider;
    private Boolean  canBeReadOnly;
    private byte[] auth;
    private byte[] defaultData;
    private CompressionProvider compressionProvider;
    private CuratorFramework client;
    private EnsembleProvider ensembleProvider;
    private Integer connectionTimeout;
    private Integer retryPolicyBaseSleepTime;
    private Integer retryPolicyMaxElapsedTime;
    private Integer retryPolicyMaxRetries;
    private Integer retryPolicyMaxSleepTime;
    private Integer retryPolicySleepBetweenRetries;
    private Integer sessionTimeout;
    private String connectionString;
    private String namespace;
    private String retryPolicyType;
    private String scheme;
    private ThreadFactory threadFactory;
    private ZookeeperFactory zookeeperFactory;


//    builder.addPropertyValue("retryPolicyType", retryPolicyTypeElement.getLocalName());
//    builder.addPropertyValue("retryPolicyMaxRetries", Integer.parseInt(retryPolicyTypeElement.getAttribute("max-retries")));
//    builder.addPropertyValue("retryPolicyBaseSleepTime", Integer.parseInt(retryPolicyTypeElement.getAttribute("base-sleep-time")));
//    builder.addPropertyValue("retryPolicyMaxSleepTime", Integer.parseInt(retryPolicyTypeElement.getAttribute("max-sleep-time")));
//    builder.addPropertyValue("retryPolicySleepBetweenRetries", Integer.parseInt(retryPolicyTypeElement.getAttribute("sleep-between-retries")));
//    builder.addPropertyValue("retryPolicyMaxElapsedTime", Integer.parseInt(retryPolicyTypeElement.getAttribute("max-elapsed-time")))

    @Override
    public void destroy() throws Exception {
        try {
            logger.info("Closing Curator client");
            if (client != null) {
                client.close();
            }
        } catch (final Exception e) {
            logger.error("Error closing Curator client: ", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        buildClient();
        validateClient();
    }

    @Override
    public CuratorFramework getObject() throws Exception {
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return CuratorFramework.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Builds an Apache Curator {@link CuratorFramework} client instance
     * from the configuration stored in this factory.
     * @throws Exception if the provided configuration is invalid or an
     * 	error occurs while attempting to build the client.
     */
    protected void buildClient() throws Exception {
        if(!StringUtils.hasText(connectionString) && ensembleProvider == null) {
            throw new IllegalArgumentException("[Assertion failed] one of 'connection-string' or 'ensemble-provider' must be configured.");
        }

        if(StringUtils.hasText(connectionString) && ensembleProvider != null) {
            throw new IllegalArgumentException("[Assertion failed] one of 'connection-string' or 'ensemble-provider' must be configured, but not both.");
        }

        final Builder builder = CuratorFrameworkFactory.builder();

        if(aclProvider != null) {
            builder.aclProvider(aclProvider);
        }

        if(StringUtils.hasText(scheme)) {
            builder.authorization(scheme, auth);
        }

        if(canBeReadOnly != null) {
            builder.canBeReadOnly(canBeReadOnly);
        }

        if(compressionProvider != null) {
            builder.compressionProvider(compressionProvider);
        }

        if(StringUtils.hasText(connectionString)) {
            builder.connectString(connectionString);
        }

        if(connectionTimeout != null) {
            builder.connectionTimeoutMs(connectionTimeout);
        }

        if(defaultData != null) {
            builder.defaultData(defaultData);
        }

        if(ensembleProvider != null) {
            builder.ensembleProvider(ensembleProvider);
        }

        if(StringUtils.hasText(namespace)) {
            builder.namespace(namespace);
        }

        builder.retryPolicy(createRetryPolicy());

        if(sessionTimeout != null) {
            builder.sessionTimeoutMs(sessionTimeout);
        }

        if(threadFactory != null) {
            builder.threadFactory(threadFactory);
        }

        if(zookeeperFactory != null) {
            builder.zookeeperFactory(zookeeperFactory);
        }

        client = builder.build();
    }

    /**
     * Validates that the newly created {@link CuratorFramework} client can
     * start and can read data from the configured ZooKeeper ensemble/host.
     * @throws Exception if an error occurs while attempting to start or
     * 	verify the client.
     */
    protected void validateClient() throws Exception {
        if(client != null) {
            client.start();
            client.checkExists().forPath("/");
        } else {
            throw new IllegalStateException("Attempt to validate Curator client before creating the client.");
        }
    }

    /**
     * Creates the {@link RetryPolicy} that is to be used when a failure occurs while attempting
     * to connect to ZooKeeper.
     * @return The {@link RetryPolicy} instance.
     * @throws IllegalArgumentException if the requested retry policy type is unknown/invalid.
     */
    protected RetryPolicy createRetryPolicy() {
        RetryPolicy retryPolicy = null;

        final CuratorRetryPolicy requestedRetryPolicy = CuratorRetryPolicy.findByType(retryPolicyType);

        if(requestedRetryPolicy != null) {
            switch(requestedRetryPolicy) {
                case BOUNDED_EXPONENTIAL_BACKOFF:
                    retryPolicy = new BoundedExponentialBackoffRetry(retryPolicyBaseSleepTime, retryPolicyMaxSleepTime, retryPolicyMaxRetries);
                    break;
                case EXPONENTIAL_BACKOFF:
                    retryPolicy = new ExponentialBackoffRetry(retryPolicyBaseSleepTime, retryPolicyMaxRetries, retryPolicyMaxSleepTime);
                    break;
                case RETRY_N_TIMES:
                    retryPolicy = new RetryNTimes(retryPolicyMaxRetries, retryPolicySleepBetweenRetries);
                    break;
                case RETRY_ONE_TIME:
                    retryPolicy = new RetryOneTime(retryPolicySleepBetweenRetries);
                    break;
                case RETRY_UNTIL_ELAPSED:
                    retryPolicy = new RetryUntilElapsed(retryPolicyMaxElapsedTime, retryPolicySleepBetweenRetries);
                    break;
                default:
                    throw new IllegalArgumentException("[Assertion failed] retry policy '" + retryPolicyType + "' is invalid/unknown.");
            }
        } else {
            throw new IllegalArgumentException("[Assertion failed] retry policy '" + retryPolicyType + "' is invalid/unknown.");
        }

        return retryPolicy;
    }

    public ACLProvider getAclProvider() {
        return aclProvider;
    }

    public void setAclProvider(final ACLProvider aclProvider) {
        this.aclProvider = aclProvider;
    }

    public Boolean isCanBeReadOnly() {
        return canBeReadOnly;
    }

    public void setCanBeReadOnly(final Boolean canBeReadOnly) {
        this.canBeReadOnly = canBeReadOnly;
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void setClient(final CuratorFramework client) {
        this.client = client;
    }

    public EnsembleProvider getEnsembleProvider() {
        return ensembleProvider;
    }

    public void setEnsembleProvider(final EnsembleProvider ensembleProvider) {
        this.ensembleProvider = ensembleProvider;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(final Integer sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getRetryPolicyType() {
        return retryPolicyType;
    }

    public void setRetryPolicyType(final String retryPolicyType) {
        this.retryPolicyType = retryPolicyType;
    }

    public byte[] getAuth() {
        return auth;
    }
    public void setAuth(final String auth) {
        this.auth = StringUtils.hasText(auth) ? auth.getBytes() : null;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(final String connectionString) {
        this.connectionString = connectionString;
    }

    public byte[] getDefaultData() {
        return defaultData;
    }

    public void setDefaultData(final String defaultData) {
        this.defaultData = StringUtils.hasText(defaultData) ? defaultData.getBytes() : null;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public void setThreadFactory(final ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public ZookeeperFactory getZookeeperFactory() {
        return zookeeperFactory;
    }

    public void setZookeeperFactory(final ZookeeperFactory zookeeperFactory) {
        this.zookeeperFactory = zookeeperFactory;
    }

    public Integer getRetryPolicyBaseSleepTime() {
        return retryPolicyBaseSleepTime;
    }

    public void setRetryPolicyBaseSleepTime(final Integer retryPolicyBaseSleepTime) {
        this.retryPolicyBaseSleepTime = retryPolicyBaseSleepTime;
    }

    public Integer getRetryPolicyMaxElapsedTime() {
        return retryPolicyMaxElapsedTime;
    }

    public void setRetryPolicyMaxElapsedTime(final Integer retryPolicyMaxElapsedTime) {
        this.retryPolicyMaxElapsedTime = retryPolicyMaxElapsedTime;
    }

    public Integer getRetryPolicyMaxRetries() {
        return retryPolicyMaxRetries;
    }

    public void setRetryPolicyMaxRetries(final Integer retryPolicyMaxRetries) {
        this.retryPolicyMaxRetries = retryPolicyMaxRetries;
    }

    public Integer getRetryPolicyMaxSleepTime() {
        return retryPolicyMaxSleepTime;
    }

    public void setRetryPolicyMaxSleepTime(final Integer retryPolicyMaxSleepTime) {
        this.retryPolicyMaxSleepTime = retryPolicyMaxSleepTime;
    }

    public Integer getRetryPolicySleepBetweenRetries() {
        return retryPolicySleepBetweenRetries;
    }

    public void setRetryPolicySleepBetweenRetries(
            final Integer retryPolicySleepBetweenRetries) {
        this.retryPolicySleepBetweenRetries = retryPolicySleepBetweenRetries;
    }
}
