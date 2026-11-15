# order-resilience — specifications by example
# Scenario tags @w<N> map items to delivery waves (see WAVES.md / BACKLOG.md).

Feature: order-resilience

  @w1 @happy-path
  Scenario: Upstream timeout returns a clean error
    Given the order service is configured with bounded upstream timeouts
    When the customer service accepts the connection but never responds
    Then POST /orders responds 504 within the configured timeout
    And the response body explains the upstream timed out

  @w1
  Scenario: Upstream connection failure returns a clean error
    Given the customer service is not running
    When a shopper places an order
    Then POST /orders responds 502
    And the response body explains the upstream is unreachable

  @w1
  Scenario: Upstream server error returns a clean error
    Given the product service returns HTTP 500
    When a shopper places an order
    Then POST /orders responds 502
    And no stack trace or internal detail leaks in the response

  @w1 @regression
  Scenario: Unknown references still return 404
    Given the customer and product services are healthy
    When a shopper places an order for an unknown customer id
    Then POST /orders responds 404
    When a shopper places an order for an unknown product id
    Then POST /orders responds 404

  @w2 @contract
  Scenario: Upstream response contract is pinned
    Given the Customer and Product APIs
    When their response payloads are inspected by a contract test
    Then every field RemotePorts reads is asserted present with its type
    And a renamed or removed upstream field fails the test loudly
