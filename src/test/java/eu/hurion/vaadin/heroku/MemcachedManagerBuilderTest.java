package eu.hurion.vaadin.heroku;

import com.google.common.collect.Lists;
import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import de.javakaffee.web.msm.MemcachedSessionService;
import org.testng.annotations.Test;

import java.util.List;

import static eu.hurion.vaadin.heroku.MemcachedManagerBuilder.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MemcachedManagerBuilderTest {

    public static final String ADAM = "Adam";
    public static final String PASSWORD = "password123!";
    public static final String URL = "memcached_url";

    @Test
    public void allBaseConfiguration() {
        List<MemcachedBackupSessionManager> managers = Lists.newArrayList(
                memcachedConfig().build(),
                memcacheAddOn().build(),
                memcachierAddOn().build());
        for (MemcachedBackupSessionManager manager : managers) {
            assertThat(manager.isSticky(), is(false));
            assertThat(manager.isSessionBackupAsync(), is(false));
            assertThat(manager.getMemcachedNodes(), is("localhost:11211"));
            assertThat(manager.getDistributable(), is(true));

            final MemcachedSessionService memcachedSessionService = manager.getMemcachedSessionService();
            assertThat(memcachedSessionService.getUsername(), is(nullValue()));
            assertThat(memcachedSessionService.getPassword(), is(nullValue()));
        }
    }

    @Test
    public void setUsername() {
        final MemcachedBackupSessionManager manager = memcachedConfig()
                .username(ADAM).build();

        final MemcachedSessionService memcachedSessionService = manager.getMemcachedSessionService();
        assertThat(memcachedSessionService.getUsername(), is(ADAM));
    }

    @Test
    public void setPassword() {
        final MemcachedBackupSessionManager manager = memcachedConfig()
                .password(PASSWORD).build();

        final MemcachedSessionService memcachedSessionService = manager.getMemcachedSessionService();
        assertThat(memcachedSessionService.getPassword(), is(PASSWORD));
    }

    @Test
    public void setUrl() {
        final MemcachedBackupSessionManager manager = memcachedConfig()
                .url(URL).build();

       assertThat(manager.getMemcachedNodes(), equalTo(URL +":" + MemcachedManagerBuilder.DEFAULT_MEMCACHEPORT));
    }

    @Test
    public void setPort() {
        final MemcachedBackupSessionManager manager = memcachedConfig()
                .port(1234).build();

        assertThat(manager.getMemcachedNodes(), equalTo(DEFAULT_URL + ":1234"));
    }

    @Test
    public void toStringIsCorrect(){
        final String configToString = memcachedConfig().toString();

        assertThat(configToString, is("MemcachedManagerBuilder{username='null', password='null', url='localhost', port=11211}"));
    }
}
