package com.kush.todo.interceptor;

import com.kush.todo.dto.CustomHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestIdInterceptor extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestId = UUID.randomUUID().toString();
            MDC.put(CustomHeaders.REQUEST_ID, requestId);
            response.setHeader(CustomHeaders.REQUEST_ID, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(CustomHeaders.REQUEST_ID);
        }
    }
}
