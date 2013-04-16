package eu.hurion.vaadin.heroku;

import org.apache.catalina.Context;
import org.testng.annotations.Test;

import javax.servlet.ServletContextListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class VaadinForHerokuTest {

    private final VaadinForHeroku server = VaadinForHeroku.forApplication(TestApplication.class);

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullApplication() {
        VaadinForHeroku.forApplication(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullStringApplicationListener() {
        final VaadinForHeroku server = VaadinForHeroku.forApplication(TestApplication.class);
        server.withApplicationListener(null, "fake listener", null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullClassApplicationListener() {
        final VaadinForHeroku server = VaadinForHeroku.forApplication(TestApplication.class);
        server.withApplicationListener((Class<? extends ServletContextListener>) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullFilterDefinition() {
        final VaadinForHeroku server = VaadinForHeroku.forApplication(TestApplication.class);
        server.withFilterDefinition(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nullFilterMapping() {
        final VaadinForHeroku server = VaadinForHeroku.forApplication(TestApplication.class);
        server.withFilterMapping(null);
    }

    @Test
    public void withApplicationListener() {
        final VaadinForHeroku withAppListener = server.withApplicationListener(MockApplicationListener.class);
        assertThat(withAppListener, equalTo(server));
    }

    @Test
    public void withFilterDefinition() {
        final VaadinForHeroku withFilter = server.withFilterDefinition(FilterDefinitionBuilder.filterDefinition("testFilter"));
        assertThat(withFilter, equalTo(server));
    }

    @Test
    public void withFilterMapping() {
        final VaadinForHeroku withFilterMapping = server.withFilterMapping(FilterMapBuilder.mapFilter("testFilter"));
        assertThat(withFilterMapping, equalTo(server));
    }

    @Test
    public void buldServerWithoutSessionManager() {
        final VaadinForHeroku.EmbedVaadinWithSessionManagement built = server.build();
        final Context contextForTest = built.getContextForTest();
        assertThat(contextForTest.getManager(), nullValue());
    }

    @Test
    public void buildServerWithSessionManager() {
        final VaadinForHeroku.EmbedVaadinWithSessionManagement built =
                server.withMemcachedSessionManager(MemcachedManagerBuilder.memcachedConfig()).build();
        built.configure();
        final Context contextForTest = built.getContextForTest();
        assertThat(contextForTest.getManager(), notNullValue());
    }
}
