@smoke
Feature: Validating Login

  Background:
    Given I am on the Product Store home page

  Scenario Outline: Validating Login
    When I click on the "Log in" button
    And User enters username "<username>" and password "<password>"
    Then User should see the welcome message with their name

    Examples:
      | username | password  |
      | pavanol  |           |
      |          | test@123  |
      | pavanol  | test@123  |

  Scenario Outline: Validating sign in
    When I click on the "Sign up" button
    And I Enter a username "<username>" and password "<password>" for signup
    And I Click on Signup Button in popup
    Then I should see the Signup failure alert

    Examples:
      | username | password |
      | Jinith   |          |
      |          | 123      |
      | jinith   | 234      |
      | July     | J123     |
