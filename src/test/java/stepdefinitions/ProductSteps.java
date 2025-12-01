package stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Driver;
import utils.Helpers;
import pages.Product;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductSteps {

    private final WebDriver driver = Driver.getDriver();
    private final Helpers helper = new Helpers(driver);

    private By categoryLink(String category) {
        return By.xpath("//a[normalize-space()='" + category + "']");
    }

    private final By productNameLoc = By.cssSelector(".col-lg-9 h4");
    private final By productPriceLoc = By.cssSelector(".col-lg-9 h5");

    private List<Product> actualProducts = new ArrayList<>();

    @When("I click on the {string} category")
    public void i_click_on_the_category(String category) {
        helper.waitAndClick(categoryLink(category));
        helper.waitForPageLoad();

        // Capture first visible product text BEFORE waiting
        String beforeFirst = "";
        try {
            // find first visible name safely (refetch element before check)
            List<WebElement> namesBefore = driver.findElements(productNameLoc);
            for (int i = 0; i < namesBefore.size(); i++) {
                try {
                    WebElement e = driver.findElements(productNameLoc).get(i); // refetch
                    if (helper.isDisplayed(e)) {
                        beforeFirst = helper.waitAndGetText(e).trim();
                        break;
                    }
                } catch (StaleElementReferenceException ignored) {
                    // try next index (or next loop iteration which will refetch)
                }
            }
        } catch (Exception ignored) {}

        final String originalFirst = beforeFirst;

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        wait.until((ExpectedCondition<Boolean>) wd -> {
            try {
                // find the first visible name at this moment (refetch)
                List<WebElement> nowList = driver.findElements(productNameLoc);
                String nowFirst = "";

                for (int i = 0; i < nowList.size(); i++) {
                    try {
                        WebElement e = driver.findElements(productNameLoc).get(i); // refetch by index
                        if (helper.isDisplayed(e)) {
                            nowFirst = helper.waitAndGetText(e).trim();
                            break;
                        }
                    } catch (StaleElementReferenceException ex) {
                        // dom changed while checking; treat as changed => success
                        return true;
                    }
                }

                // if previously empty and now non-empty => success
                if (originalFirst.isEmpty() && !nowFirst.isEmpty()) {
                    return true;
                }

                // otherwise success only if first item changed
                return !nowFirst.equals(originalFirst);
            } catch (StaleElementReferenceException | NoSuchElementException e) {
                // DOM changed while evaluating -> consider as changed (success)
                return true;
            } catch (Exception e) {
                // any other exception -> treat as success to avoid blocking tests
                return true;
            }
        });

        helper.waitForPageLoad();
    }

    @Then("I should see the following products exactly:")
    public void i_should_see_the_following_products_exactly(DataTable table) {
        // expected products
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        List<Product> expected = new ArrayList<>();
        for (Map<String, String> row : rows) {
            expected.add(new Product(row.get("Product"), row.get("Price")));
        }

        // read actual products from page (fresh)
        actualProducts = readProductsFresh();

        // debug print when counts don't match
        if (expected.size() != actualProducts.size()) {
            System.out.println("DEBUG: expected count = " + expected.size() + ", actual count = " + actualProducts.size());
            System.out.println("DEBUG: actual products:");
            for (int i = 0; i < actualProducts.size(); i++) {
                System.out.println("  [" + i + "] " + actualProducts.get(i));
            }
        }

        Assert.assertEquals("Product count mismatch!", expected.size(), actualProducts.size());

        for (int i = 0; i < expected.size(); i++) {
            Product exp = expected.get(i);
            Product act = actualProducts.get(i);
            Assert.assertEquals("Name mismatch at index " + i, exp.getName(), act.getName());
            Assert.assertEquals("Price mismatch at index " + i, exp.getPrice(), act.getPrice());
        }

        System.out.println("PRODUCTS MATCH EXACTLY");
    }

    @And("I successfully view the product name and product price")
    public void i_successfully_view_the_product_name_and_product_price() {
        if (actualProducts == null || actualProducts.isEmpty()) {
            actualProducts = readProductsFresh();
        }

        Assert.assertFalse("No products found!", actualProducts.isEmpty());

        for (Product p : actualProducts) {
            Assert.assertFalse("Product name empty", p.getName() == null || p.getName().isBlank());
            Assert.assertFalse("Product price empty", p.getPrice() == null || p.getPrice().isBlank());
        }

        System.out.println("✔ PRODUCT NAME + PRICE VISIBLE SUCCESSFULLY");
    }

    /**
     * Read product name + price pairs but only for currently visible elements.
     * For each index we re-find the element to avoid StaleElementReferenceException.
     */
    private List<Product> readProductsFresh() {
        List<Product> list = new ArrayList<>();

        // capture names safely (refetch per index)
        List<String> names = new ArrayList<>();
        int namesCount = driver.findElements(productNameLoc).size();
        for (int i = 0; i < namesCount; i++) {
            try {
                WebElement e = driver.findElements(productNameLoc).get(i); // refetch
                if (!helper.isDisplayed(e)) continue;
                names.add(helper.waitAndGetText(e).trim());
            } catch (StaleElementReferenceException e) {
                // retry once
                try {
                    WebElement e2 = driver.findElements(productNameLoc).get(i);
                    if (!helper.isDisplayed(e2)) continue;
                    names.add(helper.waitAndGetText(e2).trim());
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        }

        // capture prices safely (refetch per index)
        List<String> prices = new ArrayList<>();
        int pricesCount = driver.findElements(productPriceLoc).size();
        for (int i = 0; i < pricesCount; i++) {
            try {
                WebElement e = driver.findElements(productPriceLoc).get(i); // refetch
                if (!helper.isDisplayed(e)) continue;
                prices.add(helper.waitAndGetText(e).trim());
            } catch (StaleElementReferenceException e) {
                // retry once
                try {
                    WebElement e2 = driver.findElements(productPriceLoc).get(i);
                    if (!helper.isDisplayed(e2)) continue;
                    prices.add(helper.waitAndGetText(e2).trim());
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        }

        int count = Math.min(names.size(), prices.size());
        for (int i = 0; i < count; i++) {
            list.add(new Product(names.get(i), prices.get(i)));
        }

        return list;
    }
}
