package com.kush.todo.filter;

import com.kush.todo.BaseTest;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.service.RequestUtilsService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class UserLoggingFilterTest extends BaseTest {

    @Mock
    private RequestUtilsService requestUtilsService;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private UserLoggingFilter userLoggingFilter;

    @Test
    void doFilterInternalClearsMdcEventually() throws ServletException, IOException {
        MDC.put("testMdcKey", "testMdcValue");
        Mockito.when(currentUser.getId()).thenReturn(UUID.randomUUID());

        userLoggingFilter.doFilterInternal(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain());

        Assertions.assertNull(MDC.get("testMdcKey"));
        Assertions.assertNull(MDC.getCopyOfContextMap());
    }

    @Test
    void doFilterInternalNotFailsForNotFoundUserId() {
        Mockito.when(requestUtilsService.isAllowedEndpoint(Mockito.anyString())).thenReturn(false);
        Mockito.when(currentUser.getId()).thenThrow(new IllegalStateException());

        Assertions.assertDoesNotThrow(() -> userLoggingFilter.doFilterInternal(new MockHttpServletRequest(), new MockHttpServletResponse(), new MockFilterChain()));
    }
}