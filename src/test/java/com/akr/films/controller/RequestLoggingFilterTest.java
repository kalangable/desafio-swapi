package com.akr.films.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.HttpServlet;

class RequestLoggingFilterTest {

    private final RequestLoggingFilter filter = new RequestLoggingFilter();

    private final AtomicReference<String> requestIdSeenByTheChain = new AtomicReference<>();

    private MockFilterChain chain() {
        return new MockFilterChain(new HttpServlet() {
            @Override
            protected void service(jakarta.servlet.http.HttpServletRequest req,
                    jakarta.servlet.http.HttpServletResponse res) {
                requestIdSeenByTheChain.set(MDC.get("requestId"));
            }
        });
    }

    @Test
    void shouldGenerateARequestIdWhenTheHeaderIsMissing() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest("GET", "/films"), response, chain());

        String generated = response.getHeader("X-Request-Id");
        assertNotNull(generated);
        assertFalse(generated.isBlank());
        assertEquals(generated, requestIdSeenByTheChain.get());
    }

    @Test
    void shouldKeepTheRequestIdSentByTheCaller() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/films");
        request.addHeader("X-Request-Id", "abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, chain());

        assertEquals("abc-123", response.getHeader("X-Request-Id"));
        assertEquals("abc-123", requestIdSeenByTheChain.get());
    }

    @Test
    void shouldClearTheRequestIdFromTheLogContextAfterTheRequest() throws Exception {
        filter.doFilter(new MockHttpServletRequest("GET", "/films"), new MockHttpServletResponse(), chain());

        assertNull(MDC.get("requestId"));
    }

    @Test
    void shouldIgnoreActuatorRequests() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest("GET", "/actuator/health"), response, chain());

        assertNull(response.getHeader("X-Request-Id"));
        assertNull(requestIdSeenByTheChain.get());
        assertTrue(response.getStatus() == 200);
    }

}
