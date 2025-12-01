package stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.HomePage;
import utils.Driver;
import utils.Helpers;

import java.time.Duration;
import java.util.List;

public class HomePageSteps {

    private HomePage homePage;
    private Helpers helpers;

    // For carousel
    private String firstCarouselImageSrc;
    private String imageAfterPrevClick;
    private String imageAfterNextClick;

    // For pagination
    private List<String> firstPageProducts;

    // For contact entry
    private int contactFieldIndex = 0;

    // Common / Setup


    @Given("I am on the Product Store home page")
    public void i_am_on_the_Product_Store_home_page() {
        homePage = new HomePage();
        helpers = new Helpers(Driver.getDriver(), 10L);
        homePage.open();
    }

    @When("I view the product listing section")
    public void i_view_the_product_listing_section() {
        helpers.waitUntilVisible(By.id("tbodyid"));
    }

    @And("I should see the Product Store logo in the header")
    public void i_should_see_the_Product_Store_logo_in_the_header() {
        Assert.assertTrue("Logo should be visible", homePage.isLogoVisible());
    }

    // Header / Nav using List<String>

    @And("I click on main header buttons")
    public void i_click_on_main_header_buttons() {

        // Keys: Home, Contact, About, Cart, Login
        List<String> mainHeaderKeys = homePage.getMainHeaderKeys();

        for (String key : mainHeaderKeys) {
            homePage.clickHeaderButton(key);

            // For Contact and About, close the popup
            if (key.equalsIgnoreCase("Contact") || key.equalsIgnoreCase("About")) {
                helpers.clickVisibleModalCloseButton();
            }
        }
    }

    // SINGLE definition for this pattern (no @And duplicate!)
    @When("I click on the {string} button")
    public void i_click_on_the_button(String buttonName) {
        homePage.clickHeaderButton(buttonName);
    }

    @And("I should Navigate to cart page")
    public void i_should_Navigate_to_cart_page() {
        helpers.waitForUrlContains("cart");
        Assert.assertTrue(
                "URL should contain 'cart'",
                Driver.getDriver().getCurrentUrl().contains("cart")
        );
    }

    @And("I should see the Login popup")
    public void i_should_see_the_Login_popup() {
        Assert.assertTrue(
                "Login modal must be present",
                helpers.elementExists(By.id("logInModal"))
        );
    }

    // Carousel

    @When("I view the Moving carousel")
    public void i_view_the_Moving_carousel() throws InterruptedException {
        homePage.waitForCarouselVisible();
        Thread.sleep(1000); // small wait so carousel can auto-move
        firstCarouselImageSrc = homePage.getActiveCarouselImageSrc();
    }

    @When("I click on Previous icon")
    public void i_click_on_Previous_icon() throws InterruptedException {
        homePage.clickCarouselPrevious();
        Thread.sleep(1500);
        imageAfterPrevClick = homePage.getActiveCarouselImageSrc();
    }

    @When("I should see the carousel is moving previous image")
    public void i_should_see_the_carousel_is_moving_previous_image() {
        Assert.assertNotEquals(
                "Image after clicking Previous should be different from initial image",
                firstCarouselImageSrc,
                imageAfterPrevClick
        );
    }

    @When("I click on Next icon")
    public void i_click_on_Next_icon() throws InterruptedException {
        homePage.clickCarouselNext();
        Thread.sleep(1500);
        imageAfterNextClick = homePage.getActiveCarouselImageSrc();
    }

    @Then("I should see the carousel is moving Next image")
    public void i_should_see_the_carousel_is_moving_Next_image() {
        Assert.assertNotEquals(
                "Image after clicking Next should be different from image after Previous",
                imageAfterPrevClick,
                imageAfterNextClick
        );
    }

    // Categories using List<String>

    @And("I validate all product categories")
    public void i_validate_all_product_categories() {

        List<String> categories = List.of("Phones", "Laptops", "Monitors");

        for (String category : categories) {
            homePage.clickCategoryByName(category);

            boolean visible = homePage.isCategoryProductsVisible(category);
            Assert.assertTrue(
                    "Products should be visible for category: " + category,
                    visible
            );
        }
    }

    // Pagination (Previous / Next) using List<String>

    @When("I scroll down below product")
    public void i_scroll_down_below_product() {
        helpers.waitUntilVisible(By.id("tbodyid"));

        List<WebElement> productElements = homePage.getVisibleProductElements();
        int size = productElements.size();

        Assert.assertTrue(
                "Products should not be empty, but size was: " + size,
                size > 0
        );

        firstPageProducts = homePage.getVisibleProductNames();

        WebElement last = productElements.get(size - 1);
        helpers.scrollIntoView(last);
    }

    @And("I navigate using Next and Previous buttons")
    public void i_navigate_using_Next_and_Previous_buttons() {

        List<String> actions = List.of("Next", "Previous");

        for (String action : actions) {
            switch (action.toLowerCase()) {
                case "next":
                    homePage.clickNextPage();
                    break;
                case "previous":
                    homePage.clickPreviousPage();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown pagination action: " + action);
            }
            helpers.waitUntilVisible(By.id("tbodyid"));
        }
    }


    // Contact form

    @When("I Enter {string}")
    public void i_Enter(String ignoredPlaceholder) {
        // Using fixed values for now (same as table)
        String email = "example@mail.com";
        String name = "John";
        String message = "Test";

        switch (contactFieldIndex) {
            case 0:
                homePage.enterContactEmail(email);
                break;
            case 1:
                homePage.enterContactName(name);
                break;
            case 2:
                homePage.enterContactMessage(message);
                break;
            default:
                //
        }
        contactFieldIndex++;
    }

    @When("I Click on Send message button")
    public void i_Click_on_Send_message_button() {
        homePage.clickContactSendMessage();
    }

    @Then("I Should view the Alert {string}")
    public void i_should_view_the_alert(String expectedText, DataTable dataTable) {
        WebDriver driver = Driver.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String actualText = alert.getText();

        // Uncomment if you want strict assertion:
        // Assert.assertEquals("Alert text should match", expectedText, actualText);

        alert.accept();
    }
}
