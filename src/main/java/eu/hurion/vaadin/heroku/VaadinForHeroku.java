package eu.hurion.vaadin.heroku;

import com.bsb.common.vaadin.embed.EmbedVaadinConfig;
import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import com.bsb.common.vaadin.embed.EmbedVaadinServerBuilder;
import com.bsb.common.vaadin.embed.application.ApplicationBasedEmbedVaadinTomcat;
import com.google.common.collect.Lists;
import com.vaadin.Application;
import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import eu.hurion.vaadin.heroku.MemcachedConfigBuilder.MemcachedConfiguration;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Customized {@code EmbedVaadinApplication} to accommodate specificity of Heroku.
 *
 */
public class VaadinForHeroku extends EmbedVaadinServerBuilder<VaadinForHeroku, EmbedVaadinServer> {

    private static final Logger LOG = LoggerFactory.getLogger(VaadinForHeroku.class);

    public static final int DEFAULT_PORT = 8080;
    public static final String PORT = "PORT";

    private final Class<? extends Application> applicationClass;
    private EmbedVaadinConfig config;

    private MemcachedConfigBuilder memcachedConfigBuilder;
    private final List<FilterDefinitionBuilder> filterDefinitions = Lists.newArrayList();
    private final List<FilterMapBuilder> filterMaps = Lists.newArrayList();
    private final List<String> applicationListeners = Lists.newArrayList();

    /**
     * Creates a new instance for the specified application.
     *
     * @param applicationClass the class of the application to deploy
     */
    public VaadinForHeroku(final Class<? extends Application> applicationClass) {
        super();
        assertNotNull(applicationClass, "applicationClass could not be null.");
        this.applicationClass = applicationClass;
        withConfigProperties(EmbedVaadinConfig.loadProperties());
    }

    /**
     * Creates a bare server for the given application.
     *
     * @param applicationClass the class of the application to deploy
     */
    public static VaadinForHeroku forApplication(final Class<? extends Application> applicationClass) {
        return new VaadinForHeroku(applicationClass);
    }

    /**
     * Configures the given server for local development
     *
     * @param server the server to be configured for local development
     * @since 0.3
     */
    public static VaadinForHeroku localServer(final VaadinForHeroku server) {

        return server.withHttpPort(VaadinForHeroku.DEFAULT_PORT)
                .withProductionMode(false)
                .openBrowser(true);
    }

    /**
     * Configures the given server for Heroku
     *
     * @param server the server to be configured for heroku environment
     * @since 0.3
     */
    public static VaadinForHeroku herokuServer(final VaadinForHeroku server) {

        return server
                .withMemcachedSessionManager(MemcachedConfigBuilder.memcacheAddOn())
                .withHttpPort(Integer.parseInt(System.getenv(VaadinForHeroku.PORT)))
                .withProductionMode(true)
                .openBrowser(false);
    }

    /**
     * Configures the given server to be used in automated tests.
     *
     * @param server the server to be configured for automated testing
     * @since 0.3
     */
    public static VaadinForHeroku testServer(final VaadinForHeroku server){
        return localServer(server).wait(false).openBrowser(false);
    }
    /**
     * Returns the {@link Application} type that was used to initialize this instance, if any.
     *
     * @return the application class or <tt>null</tt> if a component was set
     */
    protected Class<? extends Application> getApplicationClass() {
        return applicationClass;
    }

    @Override
    protected VaadinForHeroku self() {
        return this;
    }

    @Override
    protected EmbedVaadinConfig getConfig() {
        return config;
    }

    @Override
    public EmbedVaadinServer build() {
        MemcachedConfiguration memcachedConfig = null;
        if (this.memcachedConfigBuilder != null) {
            memcachedConfig = memcachedConfigBuilder.build();
        }
        final List<FilterDef> filterDefs = Lists.newArrayList();
        for (FilterDefinitionBuilder filterDefinition : filterDefinitions) {
            filterDefs.add(filterDefinition.build());
        }
        final List<FilterMap> filterMappings = Lists.newArrayList();
        for (FilterMapBuilder filterMap : filterMaps) {
            filterMappings.add(filterMap.build());
        }
        LOG.debug("Memcached configuration: " + memcachedConfig);
        return new EmbedVaadinWithSessionManagement(getConfig(), getApplicationClass(),
                memcachedConfig, filterDefs, filterMappings, applicationListeners);
    }

    @Override
    public final VaadinForHeroku withConfigProperties(final Properties properties) {
        this.config = new EmbedVaadinConfig(properties);
        return self();
    }

    public VaadinForHeroku withMemcachedSessionManager(final MemcachedConfigBuilder memcachedConfigBuilder) {
        this.memcachedConfigBuilder = memcachedConfigBuilder;
        return self();
    }

    /**
     * Add filter definitions to the server configuration.
     * 
     * @param filterDefs the filter definition(s) to add.
     * @since 0.3
     */
    public VaadinForHeroku withFilterDefinition(final FilterDefinitionBuilder... filterDefs){
        this.filterDefinitions.addAll(Arrays.asList(filterDefs));
        return self();
    }

    /**
     * Add filter mappings to the server configuration.
     * A filter with the same name as the one defined in the mapping should also be defined
     * using {@link #withFilterDefinition(FilterDefinitionBuilder...)}
     * 
     * @param filterMaps the filter mapping(s)
     * @since 0.3
     */
    public VaadinForHeroku withFilterMapping(final FilterMapBuilder... filterMaps){
        this.filterMaps.addAll(Arrays.asList(filterMaps));
        return self();
    }

    /**
     * Add an application listener to the configuration of the server.
     * 
     * @param listeners the application listener(s) to add to the server configuration.
     * @since 0.3
     */
    public VaadinForHeroku withApplicationListener(final String... listeners){
        this.applicationListeners.addAll(Arrays.asList(listeners));
        return self();
    }

    /**
     * 
     * @param listeners
     * @since 0.3
     */
    public VaadinForHeroku withApplicationListener(final Class<?>... listeners){
        for (Class<?> listener : listeners) {
            applicationListeners.add(listener.getName());
        }
        return self();
    }

    /**
     * An {@link com.bsb.common.vaadin.embed.EmbedVaadinServer} implementation that will configure tomcat to store session in memcached.
     */
    private static final class EmbedVaadinWithSessionManagement extends ApplicationBasedEmbedVaadinTomcat {

        private final MemcachedConfiguration memcachedConfiguration;
        private final List<FilterDef> filterDefinitions;
        private final List<FilterMap> filterMaps;
        private final List<String> applicationListeners;

        /**
         * @param config                 the config to use
         * @param applicationClass       the class of the application to handle
         * @param memcachedConfiguration the ocnfiguration to access memcached.
 *                               If null, memcached-session-manager is not used at all and session are only stored in memory.
         * @param filterDefinitions      the list of filter definitions.
         * @param filterMaps             the list of filter maps.
         */
        private EmbedVaadinWithSessionManagement(final EmbedVaadinConfig config,
                                                 final Class<? extends Application> applicationClass,
                                                 final MemcachedConfiguration memcachedConfiguration,
                                                 final List<FilterDef> filterDefinitions,
                                                 final List<FilterMap> filterMaps,
                                                 final List<String> applicationListeners) {
            super(config, applicationClass);
            this.memcachedConfiguration = memcachedConfiguration;
            this.filterDefinitions = filterDefinitions;
            this.filterMaps = filterMaps;
            this.applicationListeners = applicationListeners;
        }

        @Override
        protected void configure() {
            super.configure();
            if (memcachedConfiguration != null) {
                final MemcachedBackupSessionManager manager = new MemcachedBackupSessionManager();
                manager.setMemcachedNodes(memcachedConfiguration.getUrl() + ":" + memcachedConfiguration.getPort());
                manager.setUsername(memcachedConfiguration.getUsername());
                manager.setPassword(memcachedConfiguration.getPassword());
                manager.setSticky(false);
                manager.setMemcachedProtocol("binary");
                manager.setRequestUriIgnorePattern(".*\\.(png|gif|jpg|css|js)$");
                getContext().setManager(manager);
            }

            for (String applicationListener : applicationListeners) {
                getContext().addApplicationListener(applicationListener);
            }
            for (FilterDef filterDefinition : filterDefinitions) {
                getContext().addFilterDef(filterDefinition);
            }
            for (FilterMap filterMap : filterMaps) {
                getContext().addFilterMap(filterMap);
            }
        }
    }

}
