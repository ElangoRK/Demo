@smoke
Feature: Validate product listing for each category

  Background:
    Given I am on the Product Store home page

  # PHONES
  Scenario: Validate all Phone category product names and prices
    When I click on the "Phones" category
    Then I should see the following products exactly:
      | Product           | Price |
      | Samsung galaxy s6 | $360  |
      | Nokia lumia 1520  | $820  |
      | Nexus 6           | $650  |
      | Samsung galaxy s7 | $800  |
      | Iphone 6 32gb     | $790  |
      | Sony xperia z5    | $320  |
      | HTC One M9        | $700  |
    And I successfully view the product name and product price

  # LAPTOPS
  Scenario: Validate all Laptop category product names and prices
    When I click on the "Laptops" category
    Then I should see the following products exactly:
      | Product             | Price |
      | Sony vaio i5        | $790  |
      | Sony vaio i7        | $790  |
      | MacBook air         | $700  |
      | Dell i7 8gb         | $700  |
      | 2017 Dell 15.6 Inch | $700  |
      | MacBook Pro         | $1100 |
    And I successfully view the product name and product price

  # MONITORS
  Scenario: Validate all Monitor category product names and prices
    When I click on the "Monitors" category
    Then I should see the following products exactly:
      | Product                          | Price |
      | Apple monitor 24                 | $400  |
      | ASUS VS247H-P 23.6- Inch Full HD | $230  |
    And I successfully view the product name and product price
