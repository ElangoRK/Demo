package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Common UI helper methods wrapped around WebDriver + WebDriverWait.
 * Used from step definitions and page objects.
 */
public class Helpers {

    private final WebDriver driver;
    private final WebDriverWait wait;

    /**
     * Convenience constructor that uses a sensible default timeout (15 seconds).
     */
    public Helpers(WebDriver driver) {
        this(driver, 15L);
    }

    /**
     * Primary constructor. Accepts a timeout in seconds (nullable).
     *
     * @param driver WebDriver instance
     * @param timeoutSeconds timeout in seconds (if null, defaults to 15)
     */
    public Helpers(WebDriver driver, Long timeoutSeconds) {
        this.driver = driver;
        long t = (timeoutSeconds == null) ? 15L : timeoutSeconds;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(t));
        this.wait.pollingEvery(Duration.ofMillis(200));
        this.wait.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
    }

    // =========================
    // Basic actions
    // =========================

    public void openUrl(String url) {
        driver.get(url);
    }

    public void click(WebElement element) {
        waitUntilClickable(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            jsClick(element);
        }
    }

    public void jsClick(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }

    /** Scroll the page until the given element is in view. */
    public void scrollIntoView(WebElement element) {
        if (element == null) return;
        try {
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception ignored) {
        }
    }

    public void type(WebElement element, String text) {
        waitUntilVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    public boolean isDisplayed(WebElement element) {
        try {
            waitUntilVisible(element);
            return element.isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    // =========================
    // Wait helpers
    // =========================

    public void waitUntilVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitUntilVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void waitUntilClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitUntilClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForUrlContains(String partial) {
        wait.until(ExpectedConditions.urlContains(partial));
    }

    public boolean elementExists(By locator) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void waitForPageLoad() {
        wait.until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    // =========================
    // Modal popup close helper
    // =========================

    /**
     * Clicks the Close button on the currently visible modal popup.
     * It waits for a modal with class 'show', then tries footer "Close" button,
     * if not clickable then tries header X button.
     */
    public void clickVisibleModalCloseButton() {
        By visibleModal = By.cssSelector("div.modal.fade.show");

        By footerCloseBtn = By.xpath(
                "//div[contains(@class,'modal') and contains(@class,'show')]" +
                        "//button[normalize-space()='Close' or @data-dismiss='modal']"
        );

        By headerCloseBtn = By.xpath(
                "//div[contains(@class,'modal') and contains(@class,'show')]" +
                        "//button[contains(@class,'close') or @aria-label='Close']"
        );

        try {
            WebElement modal = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(visibleModal)
            );

            scrollIntoView(modal);

            try {
                WebElement footerClose = wait.until(
                        ExpectedConditions.elementToBeClickable(footerCloseBtn)
                );
                try {
                    footerClose.click();
                } catch (ElementClickInterceptedException e) {
                    jsClick(footerClose);
                }
            } catch (TimeoutException | ElementClickInterceptedException e) {
                try {
                    WebElement headerClose = wait.until(
                            ExpectedConditions.elementToBeClickable(headerCloseBtn)
                    );
                    try {
                        headerClose.click();
                    } catch (ElementClickInterceptedException ex) {
                        jsClick(headerClose);
                    }
                } catch (TimeoutException ignored) {
                    // no close button found – ignore
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}

        } catch (TimeoutException e) {
            // No visible modal: do nothing
        }
    }

    // =========================
    // Extra helper actions (NON-BREAKING)
    // =========================

    /** Safe click with wait + JS fallback */
    public void safeClick(WebElement element) {
        try {
            waitUntilClickable(element);
            element.click();
        } catch (Exception e) {
            jsClick(element);
        }
    }

    /** Safe typing with visibility wait */
    public void safeType(WebElement element, String text) {
        try {
            waitUntilVisible(element);
            element.clear();
            element.sendKeys(text == null ? "" : text);
        } catch (Exception ignored) {}
    }

    /** Wait for alert, read text, accept it, return alert text. Returns null if no alert. */
    public String readAndAcceptAlert(long timeoutSeconds) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            Alert alert = shortWait.until(ExpectedConditions.alertIsPresent());
            String msg = alert.getText();
            alert.accept();
            return msg;
        } catch (Exception e) {
            return null;
        }
    }

    /** Wait and click using locator (safe) */
    public void waitAndClick(By locator) {
        try {
            waitUntilClickable(locator);
            safeClick(driver.findElement(locator));
        } catch (Exception ignored) {}
    }

    /** Wait and type using locator (safe) */
    public void waitAndType(By locator, String text) {
        try {
            waitUntilVisible(locator);
            safeType(driver.findElement(locator), text);
        } catch (Exception ignored) {}
    }

    /** Wait until visible then return text */
    public String waitAndGetText(WebElement element) {
        try {
            waitUntilVisible(element);
            return element.getText();
        } catch (Exception e) {
            return "";
        }
    }
}
