package Customer.App;

import spock.lang.*
import Customer.App.src.main.java.Customer.App.UseCases.*
import Customer.App.src.main.java.Customer.App.Services.*
import Customer.App.src.main.java.Customer.App.Repositories.*
import Customer.App.src.main.java.Customer.Domain.*
import Customer.App.src.main.java.Customer.Events.*
import Services.Common.src.main.java.Services.Common.Domain.Events.*

public class RegisterCustomerUseCaseTests extends Specification {

    IPublisherServices pubServices
    CustomerRepository customerRepository
    RegisterCustomerUseCase sutRCUseCase

    def setup() {
        pubServices = new InMemoryPublisherServices()
        customerRepository = new InMemoryCustomerRepository()
        sutRCUseCase = new RegisterCustomerUseCase(pubServices, customerRepository)
    }

    def "Should throw exception when handle null command"() {
        when:
        sutRCUseCase.handle(null)
        then:
        thrown(Exception)
    }

    def "Should be able to register a customer"() {
        when:
        sutRCUseCase.handle(new RegisterCustomer("Test", "Test", "Test", "Test", "test@example.com"))
        then:
        ((InMemoryPublisherServices) pubServices).getEvents().size() > 0
    }

    def "Should return the registered customer with a server id"() {
        when:
        def customer = sutRCUseCase.register(new RegisterCustomer("Test", "User", "1 Main St", "555-1234", "test@example.com"))
        then:
        // covers scenario: Customer gets server id
        customer.id != null
        customerRepository.findById(customer.id).isPresent()
    }

    def "Should publish a CustomerCreated domain event on registration"() {
        when:
        sutRCUseCase.register(new RegisterCustomer("Test", "User", "1 Main St", "555-1234", "test@example.com"))
        then:
        // covers scenario: Registration publishes event
        def events = ((InMemoryPublisherServices) pubServices).getEvents()
        events.size() == 1
        events[0] instanceof CustomerCreated
    }

    def "Should reject registration with missing email"() {
        when:
        sutRCUseCase.register(new RegisterCustomer("Test", "User", "1 Main St", "555-1234", "not-an-email"))
        then:
        thrown(IllegalArgumentException)
        customerRepository.findAll().isEmpty()
    }

    def "Should reject registration with blank name"() {
        when:
        sutRCUseCase.register(new RegisterCustomer(" ", "User", "1 Main St", "555-1234", "test@example.com"))
        then:
        thrown(IllegalArgumentException)
    }

    def "Should list registered customers and fetch by id"() {
        given:
        def first = sutRCUseCase.register(new RegisterCustomer("A", "One", "Addr", "111", "a@example.com"))
        def second = sutRCUseCase.register(new RegisterCustomer("B", "Two", "Addr", "222", "b@example.com"))
        def listUseCase = new ListCustomersUseCase(customerRepository)
        def getUseCase = new GetCustomerUseCase(customerRepository)

        expect:
        // covers scenario: Query customers
        listUseCase.list().size() == 2
        getUseCase.get(first.id).get().fullName.fullName == "A One"
        !getUseCase.get(UUID.randomUUID()).isPresent()
    }
}
