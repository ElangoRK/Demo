@smoke
Feature: Home Page Listing

  Scenario: Validate Home Page header and navigation options
    Given I am on the Product Store home page
    When I view the product listing section
    And I should see the Product Store logo in the header
    And I click on main header buttons
    #Then I should see all header are redirected successfully

  Scenario: Validate a carousel
    Given I am on the Product Store home page
    When I view the Moving carousel
    And I click on Previous icon
    And I should see the carousel is moving previous image
    And I click on Next icon
    Then I should see the carousel is moving Next image

  Scenario: Validating Categories
    Given I am on the Product Store home page
    And I validate all product categories

  Scenario: Validating Previous and Next
    Given I am on the Product Store home page
    When I scroll down below product
    And I navigate using Next and Previous buttons

  Scenario Outline: Submitting details on Contact
    Given I am on the Product Store home page
    When I click on the "Contact" button
    And I Enter "<Contact Email>"
    And I Enter "<Contact Name>"
    And I Enter "<Message>"
    And I Click on Send message button
    # Then I Should view the Alert "Thanks for the message!"

    Examples:
      | Contact Email     | Contact Name | Message |
      | example@mail.com  | John         | Test    |
