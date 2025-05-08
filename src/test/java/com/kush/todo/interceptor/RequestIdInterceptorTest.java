package com.kush.todo.interceptor;

import com.kush.todo.dto.CustomHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.io.IOException;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestIdInterceptorTest {

    private final RequestIdInterceptor requestIdInterceptor = new RequestIdInterceptor();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void doFilterInternal() throws ServletException, IOException {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        requestIdInterceptor.doFilterInternal(request, response, filterChain);

        Assertions.assertNull(MDC.get(CustomHeaders.REQUEST_ID));
        Assertions.assertNotNull(response.getHeader(CustomHeaders.REQUEST_ID));
    }
}