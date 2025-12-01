package core;

import org.openqa.selenium.WebDriver;
import utils.Driver;

/**
 * Thin shim so older code that calls DriverManager.getDriver() continues working.
 */
public final class DriverManager {
    private DriverManager() {}

    public static WebDriver getDriver() {
        return Driver.getDriver();
    }

    public static void quitDriver() {
        Driver.quitDriver();
    }
}
