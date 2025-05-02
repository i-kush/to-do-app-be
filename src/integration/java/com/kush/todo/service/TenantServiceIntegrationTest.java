package com.kush.todo.service;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.dto.response.TenantDto;
import com.kush.todo.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

class TenantServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TenantService tenantService;

    @Test
    void findByIdNotFound() {
        Assertions.assertThrows(
                NotFoundException.class,
                () -> tenantService.findById(UUID.randomUUID()),
                "When no info in DB then exception should be thrown"
        );
    }

    @Test
    void findById() {
        TenantDto createdTenant = tenantService.create(new TenantDto(UUID.randomUUID(), "test"));

        TenantDto foundTenant = Assertions.assertDoesNotThrow(
                () -> tenantService.findById(createdTenant.id()),
                "No exceptions on finding should appear"
        );
        Assertions.assertEquals(createdTenant, foundTenant, "Created and found entities are not the same");
    }
}
