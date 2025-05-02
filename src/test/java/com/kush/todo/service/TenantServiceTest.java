package com.kush.todo.service;

import com.kush.todo.BaseTest;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.repository.TenantRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

class TenantServiceTest extends BaseTest {

    @InjectMocks
    private TenantService tenantService;

    @Mock
    private TenantRepository tenantRepository;

    @Test
    void findById() {
        Mockito.when(tenantRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                NotFoundException.class,
                () -> tenantService.findById(UUID.randomUUID()),
                "When no info in DB then exception should be thrown"
        );
    }

}
