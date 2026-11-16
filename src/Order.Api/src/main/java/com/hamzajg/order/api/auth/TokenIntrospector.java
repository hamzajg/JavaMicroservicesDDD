package com.hamzajg.order.api.auth;

public interface TokenIntrospector {

    Introspection introspect(String token);
}
