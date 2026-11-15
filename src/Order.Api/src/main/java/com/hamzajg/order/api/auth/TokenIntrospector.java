package com.hamzajg.order.api.auth;

public interface TokenIntrospector {

    IntrospectionResult introspect(String token);
}
