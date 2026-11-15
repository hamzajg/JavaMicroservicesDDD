# product-rest-api — specifications by example
# Scenario tags @w<N> map items to delivery waves (see WAVES.md / BACKLOG.md).

Feature: product-rest-api

  @w1
  Scenario: Registration publishes event
    Given the product use case is executed with valid input
    Then a ProductCreated domain event is published

  @w1
  Scenario: Shared publisher relocation
    Given the publisher services moved to Services.Common
    Then Customer.App and Product.App both compile against them

  @w2 @happy-path
  Scenario: Register product via API
    Given the product API is running on port 8081
    When POST /products is called with a valid product payload
    Then the response status is 201
    And the response body contains a non-empty id
    When GET /products/{id} is called with that id
    Then the response contains the product's sku, name, description and price

  @w2
  Scenario: Register invalid product rejected
    Given the product API is running
    When POST /products is called with a duplicate sku
    Then the response status is 409
    When POST /products is called with a missing name
    Then the response status is 400
    And GET /products does not list the rejected products

  @w2
  Scenario: Query products
    Given two products were registered
    When GET /products is called
    Then both products are listed
    When GET /products/{unknown-id} is called
    Then the response status is 404
