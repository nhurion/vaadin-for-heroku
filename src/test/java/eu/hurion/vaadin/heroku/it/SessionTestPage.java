package eu.hurion.vaadin.heroku.it;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.util.concurrent.TimeUnit;

public class SessionTestPage {

    private final WebDriver driver;
    private final Wait<WebDriver> wait;

    private WebElement nameInput;
    private WebElement button;

    public SessionTestPage(final WebDriver driver) {
        this.driver = driver;
        wait = new FluentWait<WebDriver>(driver).withTimeout(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class,
                        ElementNotFoundException.class);

    }

    public void enterName(final String name) throws InterruptedException {
        nameInput.clear();
        nameInput.sendKeys(name);
        new Actions(driver).moveToElement(button);
        Thread.sleep(50);
    }

    public void load(final String page) {
        driver.get(page);
        nameInput = wait.until(WebDriverUtils.presenceOfElementLocated(By.id(SessionTestApplication.NAME_ID)));
        button = wait.until(WebDriverUtils.presenceOfElementLocated(By.id(SessionTestApplication.BUTTON_ID)));
    }

    public void clickButton() {
        button.click();
    }

    public void assertNameValue(final String expectedValue) {
        wait.until(new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(final WebDriver webDriver) {
                if (expectedValue.equals(nameInput.getAttribute("value"))) {
                    return nameInput;
                } else {
                    return null;
                }
            }
        });

    }
}
