package com.kush.todo.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.service.AppUserService;
import com.kush.todo.service.AuthService;
import com.kush.todo.util.RequestUtilsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserStateVerificationFilter extends OncePerRequestFilter {

    private final AppUserService appUserService;
    private final ObjectMapper objectMapper;
    private final RequestUtilsService requestUtilsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (!requestUtilsService.isAllowedEndpoint(request.getRequestURI()) && appUserService.isCurrentUserLocked()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorsDto(new ErrorDto(AuthService.ERROR_MESSAGE_USER_LOCKED))));
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
