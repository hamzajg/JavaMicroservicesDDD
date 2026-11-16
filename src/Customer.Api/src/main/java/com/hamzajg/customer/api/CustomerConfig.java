package com.hamzajg.customer.api;

import Customer.App.src.main.java.Customer.App.Repositories.CustomerRepository;
import Customer.App.src.main.java.Customer.App.Repositories.InMemoryCustomerRepository;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;
import Services.Common.src.main.java.Services.Common.Services.InMemoryPublisherServices;
import Customer.App.src.main.java.Customer.App.UseCases.FindCustomerByEmailUseCase;
import Customer.App.src.main.java.Customer.App.UseCases.GetCustomerUseCase;
import Customer.App.src.main.java.Customer.App.UseCases.IRegisterCustomerUseCase;
import Customer.App.src.main.java.Customer.App.UseCases.ListCustomersUseCase;
import Customer.App.src.main.java.Customer.App.UseCases.RegisterCustomerUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerConfig {

    @Bean
    public IPublisherServices publisherServices() {
        return new InMemoryPublisherServices();
    }

    @Bean
    public CustomerRepository customerRepository() {
        return new InMemoryCustomerRepository();
    }

    @Bean
    public IRegisterCustomerUseCase registerCustomerUseCase(IPublisherServices publisherServices,
                                                           CustomerRepository customerRepository) {
        return new RegisterCustomerUseCase(publisherServices, customerRepository);
    }

    @Bean
    public GetCustomerUseCase getCustomerUseCase(CustomerRepository customerRepository) {
        return new GetCustomerUseCase(customerRepository);
    }

    @Bean
    public ListCustomersUseCase listCustomersUseCase(CustomerRepository customerRepository) {
        return new ListCustomersUseCase(customerRepository);
    }

    @Bean
    public FindCustomerByEmailUseCase findCustomerByEmailUseCase(CustomerRepository customerRepository) {
        return new FindCustomerByEmailUseCase(customerRepository);
    }
}
