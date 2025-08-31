package com.kush.todo.util;

import com.kush.todo.config.SecurityConfig;

import org.springframework.util.AntPathMatcher;

public final class RequestUtils {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private RequestUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static boolean isAllowedEndpoint(String uri) {
        return SecurityConfig.ALLOWED_ENDPOINTS
                .stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }
}
