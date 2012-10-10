package eu.hurion.vaadin.heroku;

/**
 * Builder to define configuration to access memcached.
 */
public class MemcachedConfigBuilder {
    public static final int DEFAULT_MEMCACHEPORT = 11211;
    public static final String DEFAULT_URL = "127.0.0.1";

    private String username;
    private String password;
    private String url = DEFAULT_URL;
    private int port = DEFAULT_MEMCACHEPORT;

    private MemcachedConfigBuilder() {
    }

    public static MemcachedConfigBuilder memcachedConfig() {
        return new MemcachedConfigBuilder();
    }

    /**
     * Optional. Set the username to use to connect to memcached.
     */
    public MemcachedConfigBuilder username(final String username) {
        this.username = username;
        return this;
    }

    /**
     * Optional. Set the password to use to connect to memcached.
     */
    public MemcachedConfigBuilder password(final String password) {
        this.password = password;
        return this;
    }

    /**
     * Url to access memcached. Default to {@value #DEFAULT_URL} if not specified.
     */
    public MemcachedConfigBuilder url(final String memcachedUrl) {
        this.url = memcachedUrl;
        return this;
    }

    /**
     * Port to use to access memcahce. Default to {@value #DEFAULT_MEMCACHEPORT} if not specified
     */
    public MemcachedConfigBuilder port(final int port) {
        this.port = port;
        return this;
    }

    public MemcachedConfiguration build() {
        return new MemcachedConfiguration(username, password, url, port);
    }

    /**
     * Regroup configuration information to connect to memcached.
     * Immutable. Use {@link MemcachedConfigBuilder} to create an instance.
     */
    final class MemcachedConfiguration {
        private final String username;
        private final String password;
        private final String url;
        private final int port;

        /**
         * @param username the username to connect to memcached.
         * @param password the password to connect ot memcached.
         * @param url      the url where memcached is. Without the port
         * @param port     the port on which memcached is listening.
         */
        private MemcachedConfiguration(final String username, final String password, final String url, final int port) {
            this.username = username;
            this.password = password;
            this.url = url;
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getUrl() {
            return url;
        }

        public int getPort() {
            return port;
        }

        @Override
        public String toString() {
            return "MemcachedConfiguration{" +
                    "username='" + username + '\'' +
                    ", password= Not displayed" +
                    ", url='" + url + '\'' +
                    ", port=" + port +
                    '}';
        }
    }

    /**
     * Configuration based on system properties set by the memcacheAddOn
     */
    public static MemcachedConfigBuilder memcacheAddOn() {
        final String memcacheServers = System.getenv("MEMCACHE_SERVERS");
        return memcachedConfig()
                .username(System.getenv("MEMCACHE_USERNAME"))
                .password(System.getenv("MEMCACHE_PASSWORD"))
                .url(memcacheServers == null ? DEFAULT_URL : memcacheServers);
    }

    /**
     * Configuration based on system properties set by the memcachierAddOn
     */
    public static MemcachedConfigBuilder memcachierAddOn() {
        final String memcachierServers = System.getenv("MEMCACHIER_SERVERS");
        return memcachedConfig()
                .username(System.getenv("MEMCACHIER_USERNAME"))
                .password(System.getenv("MEMCACHIER_PASSWORD"))
                .url(memcachierServers == null ? DEFAULT_URL : memcachierServers);
    }



}
