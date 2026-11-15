# customer-rest-api — specifications by example
# Scenario tags @w<N> map items to delivery waves (see WAVES.md / BACKLOG.md).

Feature: customer-rest-api

  @w2 @happy-path
  Scenario: Register customer via API
    Given the API is running
    When POST /customers is called with a valid customer payload
    Then the response status is 201
    And the response body contains a non-empty id
    When GET /customers/{id} is called with that id
    Then the response contains the customer's name, email, address and phone

  @w2
  Scenario: Register invalid customer rejected
    Given the API is running
    When POST /customers is called with a missing email
    Then the response status is 400
    And GET /customers does not list the invalid customer

  @w2
  Scenario: Query customers
    Given two customers were registered
    When GET /customers is called
    Then both customers are listed
    When GET /customers/{unknown-id} is called
    Then the response status is 404

  @w1
  Scenario: Registration publishes event
    Given the use case is executed with valid input
    Then a CustomerCreated domain event is published

  @w1
  Scenario: Customer gets server id
    Given a customer is constructed
    Then its id is a non-null UUID
