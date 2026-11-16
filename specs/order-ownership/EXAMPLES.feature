# order-ownership — specifications by example
# Scenario tags @w<N> map items to delivery waves (see WAVES.md / BACKLOG.md).

Feature: order-ownership

  @w1 @happy-path
  Scenario: The caller identity is read from the token
    Given a caller presents a valid Bearer token
    When Order.Api introspects it against Identity's GET /identity/me
    Then the caller's email is returned and attached to the request

  @w1 @happy-path
  Scenario: A caller is resolved to their customer by email
    Given a customer exists whose email matches the caller's token email
    When Order.Api looks the email up through the Customer directory
    Then the matching customer id is returned

  @w1
  Scenario: An unknown caller email resolves to no customer
    Given no customer exists with the caller's token email
    When Order.Api looks the email up through the Customer directory
    Then no customer is returned and the request is rejected 404

  @w2 @happy-path
  Scenario: Placing an order uses the caller's identity
    Given a caller whose email maps to a registered customer
    When the caller POSTs an order with items but no customerId
    Then the order is created for the caller's customer and returns 201

  @w2
  Scenario: Listing returns only the caller's orders
    Given two customers each have placed orders
    When one caller GETs /orders
    Then only that caller's orders are returned

  @w2
  Scenario: Another customer's order is invisible
    Given an order placed by a different customer
    When the caller GETs or cancels that order by id
    Then the response is 404 and the order is untouched
