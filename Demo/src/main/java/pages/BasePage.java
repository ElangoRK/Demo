package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import utils.Driver;
import utils.Helpers;

/**
 * Base page initializes driver, helpers and PageFactory.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final Helpers elementHelper;

    public BasePage() {
        this.driver = Driver.getDriver();
        this.elementHelper = new Helpers(driver); // uses convenience constructor (default timeout)
        PageFactory.initElements(driver, this);
    }

    protected void openUrl(String url) {
        driver.get(url);
        try {
            elementHelper.waitForPageLoad();
        } catch (Exception ignored) {}
    }

    public WebDriver getDriver() {
        return this.driver;
    }
}
