package com.kush.todo.service;

import com.kush.todo.BaseTest;
import com.kush.todo.TestDataBuilder;
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

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    @Test
    void createDuplicate() {
        Mockito.when(tenantRepository.existsByName(ArgumentMatchers.any())).thenReturn(true);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                                     () -> tenantService.create(TestDataBuilder.buildTenantRequestDto()));

        Assertions.assertEquals("Tenant name already exists", exception.getMessage());
    }

    @Test
    void findByIdNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(tenantRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                                                              () -> tenantService.findById(id));

        Assertions.assertEquals(String.format("No tenant with id '%s'", id), exception.getMessage());
    }

    @Test
    void updateDuplicate() {
        Mockito.when(tenantRepository.existsByName(ArgumentMatchers.any())).thenReturn(true);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                                     () -> tenantService.update(UUID.randomUUID(), TestDataBuilder.buildTenantRequestDto()));

        Assertions.assertEquals("Tenant name already exists", exception.getMessage());
    }

    @Test
    void deleteNotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(tenantRepository.existsById(id)).thenReturn(false);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                                                              () -> tenantService.delete(id));

        Assertions.assertEquals(String.format("No tenant with id '%s'", id), exception.getMessage());
    }
}