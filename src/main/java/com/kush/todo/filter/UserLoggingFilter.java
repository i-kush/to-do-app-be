package com.kush.todo.filter;

import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.util.RequestUtilsService;
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
    private final RequestUtilsService requestUtilsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            trySetUserId(request);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private void trySetUserId(HttpServletRequest request) {
        try {
            if (!requestUtilsService.isAllowedEndpoint(request.getRequestURI())) {
                MDC.put("userId", currentUser.getId().toString());
            }
        } catch (IllegalStateException e) {
            //ignore
        }
    }

}
