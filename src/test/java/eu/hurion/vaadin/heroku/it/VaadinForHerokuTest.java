package eu.hurion.vaadin.heroku.it;

import com.bsb.common.vaadin.embed.EmbedVaadinServer;
import eu.hurion.vaadin.heroku.FilterMapBuilder;
import eu.hurion.vaadin.heroku.MockApplicationListener;
import eu.hurion.vaadin.heroku.MockFilter;
import eu.hurion.vaadin.heroku.TestApplication;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static eu.hurion.vaadin.heroku.FilterDefinitionBuilder.filterDefinition;
import static eu.hurion.vaadin.heroku.VaadinForHeroku.forApplication;
import static eu.hurion.vaadin.heroku.VaadinForHeroku.testServer;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.testng.Assert.assertEquals;

public class VaadinForHerokuTest {

    private final MockFilter filter = new MockFilter();
    private final EmbedVaadinServer localServer = testServer(forApplication(TestApplication.class))
            .withApplicationListener(MockApplicationListener.class)
            .withFilterDefinition(filterDefinition("mockFilter").withFilter(filter))
            .withFilterMapping(FilterMapBuilder.mapFilter("mockFilter").toUrlPattern("/*"))
            .build();
    private final WebDriver driver = new SharedDriver();

    @BeforeClass
    public void startup() {
        //check that the application listener is called when the application is started.
        MockApplicationListener.setShouldBeInvoked(false);
        MockApplicationListener.verify();
        localServer.start();
        MockApplicationListener.setShouldBeInvoked(true);
        MockApplicationListener.verify();
    }

    @AfterClass
    public void shutdown() {
        localServer.stop();
    }

    @Test
    public void checkLocalServer() {
        //check that the filter is called when a request is processed
        filter.verify();
        driver.get("http://localhost:8080/?restartApplication");
        final WebDriverWait wait = new WebDriverWait(driver, /*seconds=*/3);
        final WebElement element = wait.until(presenceOfElementLocated(By.id(TestApplication.TEST_LABEL_ID)));
        assertEquals(element.getText(), TestApplication.TEST_LABEL);
        filter.setExpectedInvocation(true);
        filter.verify();
    }



}
