# identity-rest-api — specifications by example
# Scenario tags @w<N> map items to delivery waves (see WAVES.md / BACKLOG.md).

Feature: identity-rest-api

  @w1
  Scenario: Registration publishes event
    Given the register-user use case is executed with valid input
    Then a UserRegistered domain event is published

  @w1
  Scenario: Password hashing
    Given a user is registered with a password
    Then the stored credential is a salted hash, never the plaintext
    And login with the plaintext password succeeds while the hash itself fails

  @w2 @happy-path
  Scenario: Register user via API
    Given the identity API is running on port 8082
    When POST /identity/register is called with a valid payload
    Then the response status is 201
    And the response body contains a non-empty id
    When POST /identity/register is called with the same email
    Then the response status is 409

  @w2
  Scenario: Login issues token
    Given a registered user
    When POST /identity/login is called with the correct password
    Then the response contains a non-empty token
    When POST /identity/login is called with a wrong password
    Then the response status is 401

  @w2
  Scenario: Authenticated me endpoint
    Given a registered and logged-in user
    When GET /identity/me is called with the bearer token
    Then the response contains the user's email and display name
    When GET /identity/me is called without a token
    Then the response status is 401
    When GET /identity/me is called with an unknown token
    Then the response status is 401
