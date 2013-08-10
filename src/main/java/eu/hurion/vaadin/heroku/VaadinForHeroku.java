package eu.hurion.vaadin.heroku;

import com.bsb.common.vaadin.embed.EmbedVaadinConfig;
import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import com.bsb.common.vaadin.embed.EmbedVaadinServerBuilder;
import com.bsb.common.vaadin.embed.application.ApplicationBasedEmbedVaadinTomcat;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.vaadin.ui.UI;
import org.apache.catalina.Context;
import org.apache.catalina.Manager;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextListener;
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

    private final Class<? extends UI> uiClass;
    private EmbedVaadinConfig config;

    private MemcachedManagerBuilder memcachedManagerBuilder;
    private final List<FilterDefinitionBuilder> filterDefinitions = Lists.newArrayList();
    private final List<FilterMapBuilder> filterMaps = Lists.newArrayList();
    private final List<String> applicationListeners = Lists.newArrayList();

    /**
     * Creates a new instance for the specified application.
     *
     * @param uiClass the class of the application to deploy
     */
    private VaadinForHeroku(final Class<? extends UI> uiClass) {
        super();
        assertNotNull(uiClass, "uiClass cannot not be null.");
        this.uiClass = uiClass;
        withConfigProperties(EmbedVaadinConfig.loadProperties());
    }

    /**
     * Creates a bare server for the given application.
     *
     * @param applicationClass the class of the application to deploy
     */
    public static VaadinForHeroku forApplication(final Class<? extends UI> applicationClass) {

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
                .withMemcachedSessionManager(MemcachedManagerBuilder.memcacheAddOn())
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
     * Returns the {@link UI} type that was used to initialize this instance, if any.
     *
     * @return the application class or <tt>null</tt> if a component was set
     */
    protected Class<? extends UI> getUiClass() {
        return uiClass;
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
    public EmbedVaadinWithSessionManagement build() {
        final Manager manager;
        LOG.debug("memcached config: " + memcachedManagerBuilder);
        if (this.memcachedManagerBuilder != null) {
            manager = memcachedManagerBuilder.build();
        } else {
            manager = null;
        }
        final List<FilterDef> filterDefs = Lists.newArrayList();
        for (final FilterDefinitionBuilder filterDefinition : filterDefinitions) {
            filterDefs.add(filterDefinition.build());
        }
        final List<FilterMap> filterMappings = Lists.newArrayList();
        for (final FilterMapBuilder filterMap : filterMaps) {
            filterMappings.add(filterMap.build());
        }

        return new EmbedVaadinWithSessionManagement(getConfig(), getUiClass(),
                manager, filterDefs, filterMappings, applicationListeners);
    }

    @Override
    public final VaadinForHeroku withConfigProperties(final Properties properties) {
        this.config = new EmbedVaadinConfig(properties);
        return self();
    }

    public VaadinForHeroku withMemcachedSessionManager(final MemcachedManagerBuilder memcachedManagerBuilder) {
        this.memcachedManagerBuilder = memcachedManagerBuilder;
        return self();
    }

    public VaadinForHeroku withoutMemcachedSessionManager(){
        this.memcachedManagerBuilder = null;
        return self();
    }

    /**
     * Add filter definitions to the server configuration.
     * 
     * @param filterDefs the filter definition(s) to add.
     * @since 0.3
     */
    public VaadinForHeroku withFilterDefinition(final FilterDefinitionBuilder... filterDefs){
        checkVarArgsArguments(filterDefs);
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
        checkVarArgsArguments(filterMaps);
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
        checkVarArgsArguments(listeners);
        this.applicationListeners.addAll(Arrays.asList(listeners));
        return self();
    }

    private void checkVarArgsArguments(final Object[] objects) {
        Preconditions.checkArgument(objects != null);
        for (Object object : objects) {
            Preconditions.checkArgument(object != null);
        }
    }

    /**
     * 
     * @param listeners class of the {@code ApplicationListener} to add in the configuration of the server.
     * @since 0.3
     */
    public VaadinForHeroku withApplicationListener(final Class<? extends ServletContextListener>... listeners){
        checkVarArgsArguments(listeners);
        for (final Class<?> listener : listeners) {
            applicationListeners.add(listener.getName());
        }
        return self();
    }

    /**
     * An {@link com.bsb.common.vaadin.embed.EmbedVaadinServer} implementation that will configure tomcat to store session in memcached.
     */
    static final class EmbedVaadinWithSessionManagement extends ApplicationBasedEmbedVaadinTomcat {

        private final Manager manager;
        private final List<FilterDef> filterDefinitions;
        private final List<FilterMap> filterMaps;
        private final List<String> applicationListeners;

        /**
         * @param config                 the config to use
         * @param applicationClass       the class of the application to handle
         * @param manager                Custom Session manager. Optional
         * @param filterDefinitions      the list of filter definitions.
         * @param filterMaps             the list of filter maps.
         */
        private EmbedVaadinWithSessionManagement(final EmbedVaadinConfig config,
                                                 final Class<? extends UI> applicationClass,
                                                 final Manager manager,
                                                 final List<FilterDef> filterDefinitions,
                                                 final List<FilterMap> filterMaps,
                                                 final List<String> applicationListeners) {
            super(config, applicationClass);
            this.manager = manager;
            this.filterDefinitions = filterDefinitions;
            this.filterMaps = filterMaps;
            this.applicationListeners = applicationListeners;
        }

        Context getContextForTest() {
            return super.getContext();
        }

        @Override
        protected void configure() {
            super.configure();
            if (manager != null) {
                getContext().setManager(manager);
            }

            for (final String applicationListener : applicationListeners) {
                getContext().addApplicationListener(applicationListener);
            }
            for (final FilterDef filterDefinition : filterDefinitions) {
                getContext().addFilterDef(filterDefinition);
            }
            for (final FilterMap filterMap : filterMaps) {
                getContext().addFilterMap(filterMap);
            }
        }
    }

}
