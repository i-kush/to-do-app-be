package com.kush.todo.util;

import com.kush.todo.config.SecurityConfig;

import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

@Service
public class RequestUtilsService {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public boolean isAllowedEndpoint(String uri) {
        return SecurityConfig.ALLOWED_ENDPOINTS
                .stream()
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }
}
