package com.kush.todo.filter;

import com.kush.todo.dto.CurrentUser;
import com.kush.todo.util.RequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserLoggingFilter extends OncePerRequestFilter {

    private final CurrentUser currentUser;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if (!RequestUtils.isAllowedEndpoint(request.getRequestURI())) {
                MDC.put("userId", currentUser.getId().toString());
            }
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

}
