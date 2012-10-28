package eu.hurion.vaadin.heroku;

import de.javakaffee.web.msm.MemcachedBackupSessionManager;

/**
 * Builder to create a tomcat {@link org.apache.catalina.Manager } that will store sessions in memcached.
 */
public class MemcachedManagerBuilder {
    public static final int DEFAULT_MEMCACHEPORT = 11211;
    public static final String DEFAULT_URL = "localhost";

    private String username;
    private String password;
    private String url = DEFAULT_URL;
    private int port = DEFAULT_MEMCACHEPORT;

    private MemcachedManagerBuilder() {
    }

    public static MemcachedManagerBuilder memcachedConfig() {
        return new MemcachedManagerBuilder();
    }

    /**
     * Optional. Set the username to use to connect to memcached.
     */
    public MemcachedManagerBuilder username(final String username) {
        this.username = username;
        return this;
    }

    /**
     * Optional. Set the password to use to connect to memcached.
     */
    public MemcachedManagerBuilder password(final String password) {
        this.password = password;
        return this;
    }

    /**
     * Url to access memcached. Default to {@value #DEFAULT_URL} if not specified.
     */
    public MemcachedManagerBuilder url(final String memcachedUrl) {
        this.url = memcachedUrl;
        return this;
    }

    /**
     * Port to use to access memcahce. Default to {@value #DEFAULT_MEMCACHEPORT} if not specified
     */
    public MemcachedManagerBuilder port(final int port) {
        this.port = port;
        return this;
    }

    public MemcachedBackupSessionManager build() {
        final MemcachedBackupSessionManager manager = new MemcachedBackupSessionManager();
        manager.setMemcachedNodes(url + ":" + port);
        manager.setUsername(username);
        manager.setPassword(password);
        manager.setSticky(false);
        manager.setSessionBackupAsync(false);
        manager.setDistributable(true);
        manager.setMemcachedProtocol("binary");
        manager.setRequestUriIgnorePattern(".*\\.(png|gif|jpg|css|js)$");
        return manager;
    }

    @Override
    public String toString() {
        return "MemcachedManagerBuilder{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", url='" + url + '\'' +
                ", port=" + port +
                '}';
    }

    /**
     * Configuration based on system properties set by the memcacheAddOn
     */
    public static MemcachedManagerBuilder memcacheAddOn() {
        final String memcacheServers = System.getenv("MEMCACHE_SERVERS");
        return memcachedConfig()
                .username(System.getenv("MEMCACHE_USERNAME"))
                .password(System.getenv("MEMCACHE_PASSWORD"))
                .url(memcacheServers == null ? DEFAULT_URL : memcacheServers);
    }

    /**
     * Configuration based on system properties set by the memcachierAddOn
     */
    public static MemcachedManagerBuilder memcachierAddOn() {
        final String memcachierServers = System.getenv("MEMCACHIER_SERVERS");
        return memcachedConfig()
                .username(System.getenv("MEMCACHIER_USERNAME"))
                .password(System.getenv("MEMCACHIER_PASSWORD"))
                .url(memcachierServers == null ? DEFAULT_URL : memcachierServers);
    }


}
