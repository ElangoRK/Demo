package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import utils.Driver;

/**
 * Hooks to initialize and quit the driver.
 */
public class Hooks {

    @Before
    public void setUp() {
        Driver.getDriver();// initialize browser
    }

    @After
    public void tearDown() {
        Driver.quitDriver();
    }
}
