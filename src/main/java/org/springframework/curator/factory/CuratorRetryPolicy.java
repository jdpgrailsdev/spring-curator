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
