package com.hamzajg.order.api.auth;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

public class BearerAuthFilter extends OncePerRequestFilter {

    /** Request attribute holding the authenticated caller's email (set when VALID). */
    public static final String CALLER_EMAIL_ATTR = "com.hamzajg.order.callerEmail";

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenIntrospector tokenIntrospector;

    public BearerAuthFilter(TokenIntrospector tokenIntrospector) {
        this.tokenIntrospector = tokenIntrospector;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = authorization != null && authorization.startsWith(BEARER_PREFIX)
                ? authorization.substring(BEARER_PREFIX.length())
                : null;

        if (token == null || token.isBlank()) {
            reject(response, HttpServletResponse.SC_UNAUTHORIZED, "missing bearer token");
            return;
        }

        Introspection introspection = tokenIntrospector.introspect(token);
        switch (introspection.getResult()) {
            case VALID:
                request.setAttribute(CALLER_EMAIL_ATTR, introspection.getEmail());
                chain.doFilter(request, response);
                break;
            case INVALID:
                reject(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid or expired token");
                break;
            default:
                reject(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "identity service unavailable");
        }
    }

    private void reject(HttpServletResponse response, int status, String message) throws IOException {
        response.sendError(status, message);
    }
}
