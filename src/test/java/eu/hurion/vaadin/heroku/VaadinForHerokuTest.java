package eu.hurion.vaadin.heroku;

import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static eu.hurion.vaadin.heroku.VaadinForHeroku.forApplication;
import static eu.hurion.vaadin.heroku.VaadinForHeroku.testServer;
import static org.testng.Assert.assertEquals;

public class VaadinForHerokuTest {

    private final EmbedVaadinServer localServer = testServer(forApplication(TestApplication.class)).build();
    private final WebDriver driver = new FirefoxDriver();

    @BeforeClass
    public void startup() {
        localServer.start();
    }

    @AfterClass
    public void shutdown() {
        localServer.stop();
        driver.close();
    }

    @Test
    public void checkLocalServer() {
        driver.get("http://localhost:8080/?restartApplication");
        final WebDriverWait wait = new WebDriverWait(driver, /*seconds=*/3);
        final WebElement element = wait.until(presenceOfElementLocated(By.id(TestApplication.TEST_LABEL_ID)));
        assertEquals(element.getText(), TestApplication.TEST_LABEL);
    }

    private Function<WebDriver, WebElement> presenceOfElementLocated(final By locator) {
        return new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(final WebDriver driver) {
                return driver.findElement(locator);
            }
        };
    }

}
