package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;


public class HomePage extends BasePage {

    private static final String HOME_URL = "https://www.demoblaze.com";

    // ===== Header buttons as KEYS (not exact visible text) =====
    private static final List<String> MAIN_HEADER_KEYS = List.of(
            "Home",
            "Contact",
            "About",
            "Cart",
            "Login"

    );
    public List<String> getMainHeaderKeys() {
        return MAIN_HEADER_KEYS;
    }

    // ===== Header / navigation =====
    @FindBy(id = "nava")
    private WebElement logo;

    @FindBy(css = "a.nav-link[href='index.html']")
    private WebElement homeBtn;

    @FindBy(xpath = "//a[normalize-space()='Contact']")
    private WebElement contactBtn;

    @FindBy(xpath = "//a[normalize-space()='About us' or normalize-space()='About']")
    private WebElement aboutBtn;

    @FindBy(id = "cartur")
    private WebElement cartBtn;

    @FindBy(id = "login2")
    private WebElement loginBtn;

    @FindBy(id = "signin2")
    private WebElement signupBtn;

    // ===== Categories (left menu) =====
    @FindBy(xpath = "//a[@id='itemc' and normalize-space()='Phones']")
    private WebElement phonesCategory;

    @FindBy(xpath = "//a[@id='itemc' and normalize-space()='Laptops']")
    private WebElement laptopsCategory;

    @FindBy(xpath = "//a[@id='itemc' and normalize-space()='Monitors']")
    private WebElement monitorsCategory;

    // ===== Carousel =====
    @FindBy(id = "carouselExampleIndicators")
    private WebElement carousel;

    @FindBy(css = "#carouselExampleIndicators .carousel-control-prev")
    private WebElement carouselPrevIcon;

    @FindBy(css = "#carouselExampleIndicators .carousel-control-next")
    private WebElement carouselNextIcon;

    @FindBy(css = "#carouselExampleIndicators .carousel-item.active img")
    private WebElement activeCarouselImage;

    // ===== Products grid & pagination =====
    @FindBy(id = "tbodyid")
    private WebElement productsContainer;

    @FindBy(id = "next2")
    private WebElement nextButton;

    @FindBy(id = "prev2")
    private WebElement prevButton;

    // ===== Contact form =====
    @FindBy(id = "recipient-email")
    private WebElement contactEmailInput;

    @FindBy(id = "recipient-name")
    private WebElement contactNameInput;

    @FindBy(id = "message-text")
    private WebElement contactMessageTextArea;

    @FindBy(xpath = "//button[@onclick='send()']")
    private WebElement contactSendMessageButton;

    public HomePage() {
        super();
    }

    // =========================
    // Navigation
    // =========================

    public void open() {
        openUrl(HOME_URL);
    }

    public boolean isLogoVisible() {
        return elementHelper.isDisplayed(logo);
    }

    private String resolveHeaderText(String key) {
        switch (key.toLowerCase().trim()) {
            case "home":
                return "Home";
            case "contact":
                return "Contact";
            case "about":
                return "About us";   // actual demoblaze text
            case "cart":
                return "Cart";
            case "login":
                return "Log in";     // actual demoblaze text
            case "signup":
            case "sign up":           // actual demoblaze text
                return "Sign up";
            default:
                throw new IllegalArgumentException("Unknown header button key: " + key);
        }
    }

    public void clickHeaderButton(String key) {
        WebElement element = null;
        String k = key.toLowerCase().trim();

        try {
            switch (k) {
                case "home":
                    element = homeBtn;
                    break;
                case "contact":
                    element = contactBtn;
                    break;
                case "about":
                case "about us":
                    element = aboutBtn;
                    break;
                case "cart":
                    element = cartBtn;
                    break;
                case "login":
                case "log in":
                    element = loginBtn;
                    break;
                case "signup":
                case "sign up":
                    element = signupBtn;
                    break;
                default:
                    // Fallback: perform a text-based lookup but with wait
                    String visibleText = resolveHeaderText(key);
                    By locator = By.xpath("//a[normalize-space()='" + visibleText + "']");
                    elementHelper.waitUntilClickable(locator);
                    element = getDriver().findElement(locator);
            }

            // Wait until clickable and then click using helper (handles intercepted exceptions)
            elementHelper.waitUntilClickable(element);
            elementHelper.click(element);

        } catch (TimeoutException | NoSuchElementException ex) {
            // Throw a clearer error so test logs show the logical key
            throw new NoSuchElementException("Header button not found or not clickable for key: " + key, ex);
        }
    }

    // Categories
    public void clickPhonesCategory() {

        elementHelper.click(phonesCategory);
    }

    public void clickLaptopsCategory() {
        elementHelper.click(laptopsCategory);
    }

    public void clickMonitorsCategory() {
        elementHelper.click(monitorsCategory);
    }

    // Known products per category to assert
    public boolean isPhoneCategoryProductsVisible() {
        By phoneProduct = By.xpath(
                "//a[contains(@class,'hrefch') and normalize-space()='Samsung galaxy s6']"
        );
        return elementHelper.elementExists(phoneProduct);
    }

    public boolean isLaptopCategoryProductsVisible() {
        By laptopProduct = By.xpath(
                "//a[contains(@class,'hrefch') and normalize-space()='Sony vaio i5']"
        );
        return elementHelper.elementExists(laptopProduct);
    }

    public boolean isMonitorCategoryProductsVisible() {
        By monitorProduct = By.xpath(
                "//a[contains(@class,'hrefch') and normalize-space()='Apple monitor 24']"
        );
        return elementHelper.elementExists(monitorProduct);
    }


    public void clickCategoryByName(String categoryName) {
        switch (categoryName.toLowerCase().trim()) {
            case "phones":
                clickPhonesCategory();
                break;
            case "laptops":
                clickLaptopsCategory();
                break;
            case "monitors":
                clickMonitorsCategory();
                break;
            default:
                throw new IllegalArgumentException("Unknown category: " + categoryName);
        }
    }


    public boolean isCategoryProductsVisible(String categoryName) {
        switch (categoryName.toLowerCase().trim()) {
            case "phones":
                return isPhoneCategoryProductsVisible();
            case "laptops":
                return isLaptopCategoryProductsVisible();
            case "monitors":
                return isMonitorCategoryProductsVisible();
            default:
                throw new IllegalArgumentException("Unknown category: " + categoryName);
        }
    }


    // Carousel

    public void waitForCarouselVisible() {
        elementHelper.waitUntilVisible(carousel);
    }

    public String getActiveCarouselImageSrc() {
        elementHelper.waitUntilVisible(activeCarouselImage);
        return activeCarouselImage.getAttribute("src");
    }

    public void clickCarouselPrevious() {
        elementHelper.click(carouselPrevIcon);
    }

    public void clickCarouselNext() {
        elementHelper.click(carouselNextIcon);
    }

    // =========================
    // Product list helpers
    // =========================

    public List<WebElement> getVisibleProductElements() {
        elementHelper.waitUntilVisible(productsContainer);

        By productLocator = By.cssSelector("#tbodyid .hrefch");

        elementHelper.waitUntilVisible(productLocator);

        return getDriver().findElements(productLocator);
    }


    public List<String> getVisibleProductNames() {
        return getVisibleProductElements()
                .stream()
                .map(WebElement::getText)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    // =========================
    // Pagination buttons
    // =========================

    public void clickNextPage() {
        elementHelper.scrollIntoView(nextButton);
        elementHelper.click(nextButton);
        elementHelper.waitUntilVisible(productsContainer);
    }

    public void clickPreviousPage() {
        elementHelper.scrollIntoView(prevButton);
        elementHelper.click(prevButton);
        elementHelper.waitUntilVisible(productsContainer);
    }

    // Contact form actions

    public void enterContactEmail(String email) {
        elementHelper.type(contactEmailInput, email);
    }

    public void enterContactName(String name) {
        elementHelper.type(contactNameInput, name);
    }

    public void enterContactMessage(String message) {
        elementHelper.type(contactMessageTextArea, message);
    }

    public void clickContactSendMessage() {
        elementHelper.click(contactSendMessageButton);
    }
}
