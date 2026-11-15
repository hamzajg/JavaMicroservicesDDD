# order-rest-api — specifications by example
# Scenario tags @w<N> map items to delivery waves (see WAVES.md / BACKLOG.md).

Feature: order-rest-api

  @w1
  Scenario: Order events published
    Given the place-order use case runs with valid input
    Then an OrderPlaced domain event is published
    When the order is cancelled
    Then an OrderCancelled domain event is published

  @w2 @happy-path
  Scenario: Place order via API
    Given a registered customer and two products exist
    When POST /orders is called with the customer id and product ids + quantities
    Then the response status is 201
    And each line carries the catalog sku, name and unit price
    And the order total equals the sum of line totals

  @w2
  Scenario: Place order rejected cleanly
    Given an unknown customer id
    When POST /orders is called
    Then the response status is 404
    When POST /orders is called with an unknown product id
    Then the response status is 404
    When POST /orders is called with quantity zero
    Then the response status is 400
    And GET /orders does not list the rejected orders

  @w2
  Scenario: Query orders
    Given two orders were placed
    When GET /orders is called
    Then both orders are listed
    When GET /orders/{unknown-id} is called
    Then the response status is 404

  @w2
  Scenario: Cancel order
    Given a placed order
    When POST /orders/{id}/cancel is called
    Then the order status becomes CANCELLED
    When POST /orders/{id}/cancel is called again
    Then the response status is 409
