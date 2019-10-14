package Customer.App;

import spock.lang.*
import Customer.App.src.main.java.Customer.App.UseCases.*

import Customer.App.src.main.java.Customer.App.Services.*

public class RegisterCustomerUseCaseTests extends Specification {

    IPublisherServices mockPubServices
    RegisterCustomer registerCustomer

    RegisterCustomerUseCase sutRCUseCase

    def "Should throw exception when handle null command"() {
        given:
        mockPubServices = Mock(InMemoryPublisherServices.class)
        registerCustomer = null
        sutRCUseCase = new RegisterCustomerUseCase(mockPubServices)
        when:
        sutRCUseCase.handle(registerCustomer)
        then:
        thrown(Exception)
    }

    def "Should ba able to register a customer"() {
        given:
        mockPubServices = Spy(InMemoryPublisherServices.class)
        registerCustomer = new RegisterCustomer("Test", "Test", "Test", "Test", "Test")
        sutRCUseCase = new RegisterCustomerUseCase(mockPubServices)
        when:
        sutRCUseCase.handle(registerCustomer)
        then:
        ((InMemoryPublisherServices) mockPubServices).getEvents().size() > 0

    }

}