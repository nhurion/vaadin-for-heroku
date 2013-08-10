package eu.hurion.vaadin.heroku.it;

import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import com.google.common.collect.Lists;
import eu.hurion.vaadin.heroku.VaadinForHeroku;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Random;

import static eu.hurion.vaadin.heroku.MemcachedManagerBuilder.memcachedConfig;
import static eu.hurion.vaadin.heroku.VaadinForHeroku.forApplication;
import static eu.hurion.vaadin.heroku.VaadinForHeroku.testServer;

public class SessionTest {
    public static final String TEST_MEMCACHED_ADDRESS = "localhost";
    public static final int TEST_MEMCACHED_PORT = 11211;
    private final VaadinForHeroku localServer =
            testServer(forApplication(SessionTestApplication.class))
                    .withMemcachedSessionManager(
                            memcachedConfig()
                                    .url(TEST_MEMCACHED_ADDRESS)
                                    .port(TEST_MEMCACHED_PORT));

    private WebDriver driver;
    private SessionTestPage page;

    private final List<EmbedVaadinServer> servers = Lists.newArrayList();

    @BeforeClass
    protected void checkEnvironment() {
        if (!memcachedAvailable()) {
            throw new SkipException("Skipping tests because memcached was not available.");
        } else {
            driver = new SharedDriver();
            page = new SessionTestPage(driver);
            startServers();
        }
    }

    private boolean memcachedAvailable() {
        return connectionPossible(TEST_MEMCACHED_ADDRESS, TEST_MEMCACHED_PORT);
    }

    private boolean connectionPossible(final String address, final int port) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(address, port));
        } catch (IOException e) {
            return false;
        } finally {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    throw new RuntimeException("hell", e);
                }
            }
        }
        return true;
    }

    @AfterClass
    public void shutdown() throws InterruptedException {
        stopServers();
    }

    public void startServers() {
        startServer(localServer.withHttpPort(5000));
        startServer(localServer.withHttpPort(5001));
        startServer(localServer.withHttpPort(4000).withMemcachedSessionManager(null));
    }

    private void startServer(final VaadinForHeroku server) {
        final EmbedVaadinServer builtServer = server.build();
        servers.add(builtServer);
        builtServer.start();
    }

    public void stopServers() throws InterruptedException {
        Thread.sleep(250);
        for (final EmbedVaadinServer server : servers) {
            server.stop();
        }
        servers.clear();
    }

    private final String[] testValues = {"John", "Paul", "Georges", "Ringo", "Nicolas", "Pierre", "Peter", "Johan"};
    private final Random random = new Random();


    @Test(invocationCount = 5)
    public void oneServerNoMemcachedRefresh() throws InterruptedException {
        final String testValue = randomTestValue();

        page.load("http://localhost:4000/?restartApplication");
        page.enterName(testValue);
        page.clickButton();
        page.assertNameValue(testValue);
        page.load("http://localhost:4000/");
        page.assertNameValue(testValue);
    }

    private String randomTestValue() {
        return testValues[random.nextInt(testValues.length)];
    }

    @Test(invocationCount = 5)
    public void oneServerMemcachedRefresh() throws InterruptedException {
        final String testValue = randomTestValue();

        page.load("http://localhost:5000/?restartApplication");
        page.enterName(testValue);
        page.clickButton();
        page.assertNameValue(testValue);
        page.load("http://localhost:5000/");
        page.assertNameValue(testValue);
    }

    @Test(invocationCount = 5)
    public void twoServersShareSameSessionWithMemcached() throws InterruptedException {
        final String testValue = randomTestValue();

        page.load("http://localhost:5000/?restartApplication");
        page.enterName(testValue);
        page.clickButton();
        page.assertNameValue(testValue);
        page.load("http://localhost:5001/");
        page.assertNameValue(testValue);
    }

}
