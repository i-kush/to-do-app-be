package com.kush.todo.filter;

import com.kush.todo.mapper.AuthMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.slf4j.MDC;

import java.io.IOException;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class UserLoggingFilterTest {

    private final UserLoggingFilter userLoggingFilter = new UserLoggingFilter(Mappers.getMapper(AuthMapper.class));

    @Test
    void doFilterInternalClearsMdcEventually() throws ServletException, IOException {
        MDC.put("testMdcKey", "testMdcValue");
        userLoggingFilter.doFilterInternal(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        Assertions.assertNull(MDC.get("testMdcKey"));
        Assertions.assertNull(MDC.getCopyOfContextMap());
    }
}