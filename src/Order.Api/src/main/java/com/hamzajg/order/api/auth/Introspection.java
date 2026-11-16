package com.hamzajg.order.api.auth;

/**
 * The outcome of introspecting a bearer token, plus the caller identity it
 * proved. When the result is {@link IntrospectionResult#VALID}, {@code email}
 * is the authenticated caller's email (as returned by Identity's
 * {@code GET /identity/me}); otherwise it is {@code null}.
 */
public class Introspection {

    private final IntrospectionResult result;
    private final String email;

    public Introspection(IntrospectionResult result, String email) {
        this.result = result;
        this.email = email;
    }

    public static Introspection of(IntrospectionResult result) {
        return new Introspection(result, null);
    }

    public IntrospectionResult getResult() {
        return result;
    }

    /** The authenticated caller's email, or {@code null} when not VALID. */
    public String getEmail() {
        return email;
    }
}
