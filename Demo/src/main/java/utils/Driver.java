package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Minimal ThreadLocal WebDriver manager for simple tests.
 * Adjust browser/options as you require (ChromeOptions, headless, etc).
 */
public class Driver {

    private static final ThreadLocal<WebDriver> THREAD_DRIVER = new ThreadLocal<>();

    private Driver() { /* utility class */ }

    public static WebDriver getDriver() {
        if (THREAD_DRIVER.get() == null) {
            // Ensure you have the io.github.bonigarcia:webdrivermanager dependency
            WebDriverManager.chromedriver().setup();
            THREAD_DRIVER.set(new ChromeDriver());
        }
        return THREAD_DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = THREAD_DRIVER.get();
        if (driver != null) {
            driver.quit();
            THREAD_DRIVER.remove();
        }
    }
}
