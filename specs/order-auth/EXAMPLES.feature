# order-auth — specifications by example
# Scenario tags @w<N> map items to delivery waves (see WAVES.md / BACKLOG.md).

Feature: order-auth

  @w1 @happy-path
  Scenario: Valid token reaches order logic
    Given a shopper holds a token issued by Identity login
    When they POST /orders with "Authorization: Bearer <token>"
    Then the request reaches order placement and returns 201
    And the order is created normally

  @w1
  Scenario: Missing token is rejected
    Given a shopper has no token
    When they call any /orders endpoint without an Authorization header
    Then the response is 401
    And no order logic runs

  @w1
  Scenario: Invalid token is rejected
    Given a shopper presents a forged or expired token
    When they POST /orders with "Authorization: Bearer <bad-token>"
    Then Identity introspection returns 401 and the response is 401
    And no order logic runs

  @w1
  Scenario: Identity outage fails closed
    Given the Identity service is not running
    When a shopper calls POST /orders with any token
    Then the response is 503 within the configured timeout
    And no order logic runs

  @w1
  Scenario: Introspection call is bounded
    Given Identity accepts the connection but never responds
    When a shopper calls POST /orders
    Then the introspection call aborts at the configured timeout
    And the response is 503 or 504, never a hang
