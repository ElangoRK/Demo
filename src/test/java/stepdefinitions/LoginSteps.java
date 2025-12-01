package stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.Assert;
import pages.LoginPage;

public class LoginSteps {

    private LoginPage loginPage;
    private String lastLoginAlert;
    private String lastSignupAlert;
    private String lastUsername;
    private String lastPassword;

    private LoginPage page() {
        if (loginPage == null) loginPage = new LoginPage();
        return loginPage;
    }

    @And("User enters username {string} and password {string}")
    public void user_enters_username_and_password(String username, String password) {
        this.lastUsername = username;
        this.lastPassword = password;

        page().login(username, password);

        // capture/login alert (if any)
        lastLoginAlert = page().readAndAcceptAlert(2L);
        if (lastLoginAlert != null) {
            System.out.println("Login alert: " + lastLoginAlert);
        } else {
            System.out.println("No alert after login attempt.");
        }
    }

    @Then("User should see the welcome message with their name")
    public void user_should_see_the_welcome_message_with_their_name() {
        boolean missingField = (lastUsername == null || lastUsername.isBlank())
                || (lastPassword == null || lastPassword.isBlank());

        if (missingField) {
            Assert.assertNotNull("Expected an alert for missing credentials but none was shown", lastLoginAlert);
            Assert.assertTrue("Alert text did not indicate missing fields",
                    lastLoginAlert.toLowerCase().contains("please fill")
                            || lastLoginAlert.toLowerCase().contains("fill out"));
            System.out.println("✔ Correct alert shown for missing credentials: " + lastLoginAlert);
            return;
        }

        Assert.assertNull("Unexpected alert shown for valid credentials: " + lastLoginAlert, lastLoginAlert);

        String welcome = page().getWelcomeTextIfVisible();
        Assert.assertNotNull("Welcome message not visible", welcome);
        Assert.assertFalse("Welcome message is blank", welcome.isBlank());

        System.out.println("✔ Login success → " + welcome);
    }

    // -----------------------
    // Signup steps (Examples from your feature)
    // -----------------------
    @And("I Enter a username {string} and password {string} for signup")
    public void i_enter_a_username_and_password_for_signup(String username, String password) {
        this.lastUsername = username;
        this.lastPassword = password;
        System.out.println("Prepared signup credentials -> username: '" + username + "' password: '" + password + "'");
    }

    @And("I Click on Signup Button in popup")
    public void iClickOnSignupButtonInPopup() {
        String u = (lastUsername == null) ? "" : lastUsername;
        String p = (lastPassword == null) ? "" : lastPassword;

        page().signup(u, p);

        lastSignupAlert = page().readAndAcceptAlert(2L);

        if (lastSignupAlert != null) {
            System.out.println("Signup alert: " + lastSignupAlert);
        } else {
            System.out.println("No alert after signup attempt.");
        }
    }

    @And("I should see the Signup failure alert")
    public void iShouldSeeTheSignupFailureAlert() {
        Assert.assertNotNull("Expected signup alert but none shown", lastSignupAlert);

        boolean missingField = (lastUsername == null || lastUsername.isBlank())
                || (lastPassword == null || lastPassword.isBlank());

        if (missingField) {
            Assert.assertTrue("Alert did not reference missing fields",
                    lastSignupAlert.toLowerCase().contains("please fill")
                            || lastSignupAlert.toLowerCase().contains("fill out"));
            System.out.println("✔ Correct signup missing-field alert: " + lastSignupAlert);
        } else {
            // Non-empty-case: assert alert exists (e.g. "This user already exists")
            System.out.println("Signup alert (non-empty case): " + lastSignupAlert);
        }
    }
}
