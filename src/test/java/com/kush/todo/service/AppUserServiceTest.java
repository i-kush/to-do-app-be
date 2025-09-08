package com.kush.todo.service;

import com.kush.todo.BaseTest;
import com.kush.todo.TestDataBuilder;
import com.kush.todo.constant.CommonErrorMessages;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.AppUserMapper;
import com.kush.todo.repository.AppUserRepository;
import com.kush.todo.validator.AppUserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

class AppUserServiceTest extends BaseTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private AppUserMapper appUserMapper;

    @Mock
    private AppUserValidator appUserValidator;

    @InjectMocks
    private AppUserService appUserService;

    @BeforeEach
    void setUp() {
        Mockito.when(currentUser.getTenantId()).thenReturn(TestDataBuilder.DEFAULT_TENANT_ID);
    }

    @Test
    void deleteNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(appUserRepository.existsByIdAndTenantId(id, TestDataBuilder.DEFAULT_TENANT_ID)).thenReturn(false);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                                                              () -> appUserService.delete(id));

        Assertions.assertEquals(String.format(CommonErrorMessages.PATTERN_NOT_FOUND, id), exception.getMessage());
    }
}