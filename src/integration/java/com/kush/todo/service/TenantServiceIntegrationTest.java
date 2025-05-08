package com.kush.todo.service;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

class TenantServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TenantService tenantService;

    @Test
    void create() {
        TenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildTenantRequestDto();

        TenantResponseDto createdTenant = Assertions.assertDoesNotThrow(() -> tenantService.create(tenantRequestDto));

        Assertions.assertNotNull(createdTenant);
        Assertions.assertNotNull(createdTenant.id());
        Assertions.assertEquals(tenantRequestDto.name(), createdTenant.name());
    }

    @Test
    void createDuplicate() {
        TenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildTenantRequestDto();

        Assertions.assertDoesNotThrow(() -> tenantService.create(tenantRequestDto));
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> tenantService.create(tenantRequestDto));
    }

    @Test
    void findById() {
        TenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildTenantRequestDto();

        TenantResponseDto createdTenant = Assertions.assertDoesNotThrow(() -> tenantService.create(tenantRequestDto));
        TenantResponseDto foundTenant = Assertions.assertDoesNotThrow(() -> tenantService.findById(createdTenant.id()));

        Assertions.assertEquals(createdTenant, foundTenant);
    }

    @Test
    void findByIdNotFound() {
        Assertions.assertThrows(NotFoundException.class,
                                () -> tenantService.findById(UUID.randomUUID()));
    }

    @Test
    void update() {
        String expectedUpdatedName = "Updated Name";
        TenantRequestDto updateTenantRequestDto = IntegrationTestDataBuilder.buildTenantRequestDto(expectedUpdatedName);
        TenantRequestDto createTenantRequestDto = IntegrationTestDataBuilder.buildTenantRequestDto();

        TenantResponseDto createdTenant = Assertions.assertDoesNotThrow(() -> tenantService.create(createTenantRequestDto));
        TenantResponseDto updatedTenant = Assertions.assertDoesNotThrow(() -> tenantService.update(createdTenant.id(), updateTenantRequestDto));

        Assertions.assertNotNull(updatedTenant);
        Assertions.assertEquals(createdTenant.id(), updatedTenant.id());
        Assertions.assertNotEquals(createTenantRequestDto.name(), updatedTenant.name());
        Assertions.assertEquals(expectedUpdatedName, updatedTenant.name());
    }

    @Test
    void delete() {
        TenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildTenantRequestDto();

        TenantResponseDto createdTenant = Assertions.assertDoesNotThrow(() -> tenantService.create(tenantRequestDto));
        Assertions.assertDoesNotThrow(() -> tenantService.delete(createdTenant.id()));
    }

    @Test
    void deleteNotFound() {
        Assertions.assertThrows(NotFoundException.class,
                                () -> tenantService.delete(UUID.randomUUID()));
    }

    @Test
    void findAll() {
        int count = ThreadLocalRandom.current().nextInt(5, 15);
        IntegrationTestDataBuilder.createTenants(tenantService, count);
        CustomPage<TenantResponseDto> tenants = Assertions.assertDoesNotThrow(() -> tenantService.findAll(1, count));

        Assertions.assertNotNull(tenants);
        Assertions.assertFalse(CollectionUtils.isEmpty(tenants.items()));
        Assertions.assertEquals(count, tenants.items().size());
        Assertions.assertEquals(1, tenants.totalPages());
        Assertions.assertEquals(count, tenants.totalElements());
    }
}
