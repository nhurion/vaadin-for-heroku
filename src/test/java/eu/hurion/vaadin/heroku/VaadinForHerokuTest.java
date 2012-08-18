package eu.hurion.vaadin.heroku;

import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static eu.hurion.vaadin.heroku.VaadinForHeroku.localServer;

public class VaadinForHerokuTest {
    private final EmbedVaadinServer localServer = localServer(TestApplication.class).wait(false).openBrowser(false).build();
    private final WebDriver driver = new FirefoxDriver();

    @BeforeClass
    public void startup(){
        localServer.start();
    }

    @AfterClass
    public void shutdown(){
        localServer.stop();
        driver.close();
    }

    @Test
    public void checkLocalDriver(){
        driver.get("http://localhost:8080/?restartApplication");
        Assert.assertFalse(driver.findElements(By.id(TestApplication.TEST_LABEL_ID)).isEmpty());
    }
}
