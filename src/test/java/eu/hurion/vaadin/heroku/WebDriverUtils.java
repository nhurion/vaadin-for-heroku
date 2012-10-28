package eu.hurion.vaadin.heroku;

import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebDriverUtils {

    public static Function<WebDriver, WebElement> presenceOfElementLocated(final By locator) {
        return new Function<WebDriver, WebElement>() {
            @Override
            public WebElement apply(final WebDriver driver) {
                return driver.findElement(locator);
            }
        };
    }
}
