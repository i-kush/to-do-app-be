package com.kush.todo.validator;

import com.kush.todo.BaseTest;
import com.kush.todo.TestDataBuilder;
import com.kush.todo.entity.Tenant;
import com.kush.todo.service.TenantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TenantValidatorTest extends BaseTest {

    private TenantValidator validator = new TenantValidator();

    @Test
    void validateTenantDeletionSuccess() {
        Assertions.assertDoesNotThrow(() -> validator.validateTenantDeletion(TestDataBuilder.buildTenant()));
    }

    @Test
    void validateTenantDeletionFailure() {
        Tenant tenant = TestDataBuilder.buildTenant(TenantService.SYSTEM_TENANT_NAME);
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                                     () -> validator.validateTenantDeletion(tenant));
        Assertions.assertTrue(exception.getMessage().contains(tenant.id().toString()));
    }
}