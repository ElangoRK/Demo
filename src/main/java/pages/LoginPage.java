package pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class LoginPage extends BasePage {

    // ----- Login modal elements -----
    @FindBy(id = "loginusername")
    private WebElement usernameField;

    @FindBy(id = "loginpassword")
    private WebElement passwordField;

    @FindBy(xpath = "//button[normalize-space()='Log in' or @onclick='logIn()']")
    private WebElement loginButton;

    // ----- Signup modal elements (kept simple & tolerant) -----
    @FindBy(id = "sign-username")
    private WebElement signupUsernameField;

    @FindBy(id = "sign-password")
    private WebElement signupPasswordField;

    @FindBy(xpath = "//button[normalize-space()='Sign up' or @onclick='signUp()' or @onclick='signup()' or contains(@onclick,'sign')]")
    private WebElement signupButton;

    // ----- Post-login element -----
    @FindBy(id = "nameofuser")
    private WebElement nameOfUser; // visible after successful login

    public LoginPage() {
        super();
        PageFactory.initElements(getDriver(), this);
    }


    // Login flow

    /**
     * Fill login modal and submit. Accepts nulls and sends empty string if null.
     */
    public void login(String username, String password) {
        // Wait & scroll to username input to ensure modal visible / focused
        elementHelper.waitUntilClickable(usernameField);
        elementHelper.scrollIntoView(usernameField);

        elementHelper.safeType(usernameField, username == null ? "" : username);
        elementHelper.safeType(passwordField, password == null ? "" : password);
        elementHelper.safeClick(loginButton);
    }


    // Signup flow

    /**
     * Fill signup modal and submit.
     * If fields are missing on the page this method will still attempt actions safely.
     */
    public void signup(String username, String password) {
        try {
            elementHelper.waitUntilClickable(signupUsernameField);
            elementHelper.scrollIntoView(signupUsernameField);
            elementHelper.safeType(signupUsernameField, username == null ? "" : username);
            elementHelper.safeType(signupPasswordField, password == null ? "" : password);
            elementHelper.safeClick(signupButton);
        } catch (Exception e) {
            // If signup modal layout differs, still attempt what we can and let caller inspect alerts.
            System.out.println("Warning: signup interaction encountered an issue: " + e.getMessage());
        }
    }

    // -----------------------
    // Result helpers
    // -----------------------
    /**
     * Returns welcome text (e.g. "Welcome pavanol") if it's visible — otherwise null.
     * Uses elementHelper wait methods and handles unexpected alerts quietly.
     */
    public String getWelcomeTextIfVisible() {
        try {
            elementHelper.waitUntilVisible(nameOfUser);
            String txt = elementHelper.waitAndGetText(nameOfUser);
            return (txt == null || txt.isBlank()) ? null : txt;
        } catch (TimeoutException te) {
            return null;
        } catch (UnhandledAlertException ae) {
            // Accept unexpected alert and return null (caller can then read alert via Helpers)
            try {
                getDriver().switchTo().alert().accept();
            } catch (Exception ignored) {}
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Small wrapper around Helpers.readAndAcceptAlert so callers can easily capture alert text.
     * Keep the same long parameter type as Helpers method to avoid confusion.
     */
    public String readAndAcceptAlert(long timeoutSeconds) {
        return elementHelper.readAndAcceptAlert(timeoutSeconds);
    }

    /**
     * Convenience check: no alert and welcome text present.
     */
    public boolean isLoginSuccessful() {
        String alert = readAndAcceptAlert(2L);
        if (alert != null) {
            System.out.println("Login unsuccessful alert: " + alert);
            return false;
        }
        String welcome = getWelcomeTextIfVisible();
        return welcome != null && !welcome.isBlank();
    }
}
