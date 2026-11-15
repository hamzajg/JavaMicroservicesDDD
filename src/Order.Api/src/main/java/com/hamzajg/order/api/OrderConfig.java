package com.hamzajg.order.api;

import Order.App.src.main.java.Order.App.Ports.CustomerDirectory;
import Order.App.src.main.java.Order.App.Ports.ProductCatalog;
import Order.App.src.main.java.Order.App.Repositories.InMemoryOrderRepository;
import Order.App.src.main.java.Order.App.Repositories.OrderRepository;
import Order.App.src.main.java.Order.App.UseCases.CancelOrderUseCase;
import Order.App.src.main.java.Order.App.UseCases.GetOrderUseCase;
import Order.App.src.main.java.Order.App.UseCases.ICancelOrderUseCase;
import Order.App.src.main.java.Order.App.UseCases.IPlaceOrderUseCase;
import Order.App.src.main.java.Order.App.UseCases.ListOrdersUseCase;
import Order.App.src.main.java.Order.App.UseCases.PlaceOrderUseCase;
import Services.Common.src.main.java.Services.Common.Services.IPublisherServices;
import Services.Common.src.main.java.Services.Common.Services.InMemoryPublisherServices;
import com.hamzajg.order.api.auth.BearerAuthFilter;
import com.hamzajg.order.api.auth.TokenIntrospector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OrderConfig {

    @Bean
    public RestTemplate restTemplate(
            @Value("${upstream.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${upstream.read-timeout-ms:5000}") int readTimeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        return new RestTemplate(factory);
    }

    @Bean
    public FilterRegistrationBean<BearerAuthFilter> bearerAuthFilter(TokenIntrospector tokenIntrospector) {
        FilterRegistrationBean<BearerAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new BearerAuthFilter(tokenIntrospector));
        registration.addUrlPatterns("/orders/*");
        return registration;
    }

    @Bean
    public IPublisherServices publisherServices() {
        return new InMemoryPublisherServices();
    }

    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }

    @Bean
    public CustomerDirectory customerDirectory(RemotePorts remotePorts) {
        return remotePorts.customerDirectory();
    }

    @Bean
    public ProductCatalog productCatalog(RemotePorts remotePorts) {
        return remotePorts.productCatalog();
    }

    @Bean
    public IPlaceOrderUseCase placeOrderUseCase(IPublisherServices publisherServices,
                                               OrderRepository orderRepository,
                                               CustomerDirectory customerDirectory,
                                               ProductCatalog productCatalog) {
        return new PlaceOrderUseCase(publisherServices, orderRepository, customerDirectory, productCatalog);
    }

    @Bean
    public ICancelOrderUseCase cancelOrderUseCase(IPublisherServices publisherServices,
                                                 OrderRepository orderRepository) {
        return new CancelOrderUseCase(publisherServices, orderRepository);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepository orderRepository) {
        return new GetOrderUseCase(orderRepository);
    }

    @Bean
    public ListOrdersUseCase listOrdersUseCase(OrderRepository orderRepository) {
        return new ListOrdersUseCase(orderRepository);
    }
}
