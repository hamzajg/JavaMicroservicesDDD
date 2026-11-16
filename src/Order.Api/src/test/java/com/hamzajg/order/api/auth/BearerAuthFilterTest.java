package com.hamzajg.order.api.auth;

import javax.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;

public class BearerAuthFilterTest {

    private static final FilterChain NO_OP = (request, response) -> { };

    // covers scenario: The caller identity is read from the token
    @Test
    void validTokenAttachesCallerEmailToRequest() throws Exception {
        BearerAuthFilter filter = new BearerAuthFilter(
                token -> new Introspection(IntrospectionResult.VALID, "jane@example.com"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer good-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, NO_OP);

        assertEquals("jane@example.com", request.getAttribute(BearerAuthFilter.CALLER_EMAIL_ATTR));
        assertEquals(200, response.getStatus());
    }

    @Test
    void invalidTokenIsRejectedAndAttachesNothing() throws Exception {
        BearerAuthFilter filter = new BearerAuthFilter(
                token -> Introspection.of(IntrospectionResult.INVALID));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer forged");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, NO_OP);

        assertNull(request.getAttribute(BearerAuthFilter.CALLER_EMAIL_ATTR));
        assertEquals(MockHttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }
}
