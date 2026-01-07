package com.kush.todo.filter;

import com.kush.todo.constant.CommonErrorMessages;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.service.AppUserService;
import com.kush.todo.service.RequestUtilsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Service
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
            response.getWriter().write(objectMapper.writeValueAsString(new ErrorsDto(new ErrorDto(CommonErrorMessages.USER_LOCKED))));
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
