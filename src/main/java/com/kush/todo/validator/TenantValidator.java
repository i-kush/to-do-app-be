package com.kush.todo.validator;

import com.kush.todo.entity.Tenant;
import com.kush.todo.service.TenantService;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TenantValidator {

    public void validateTenantDeletion(Tenant tenant) {
        Assert.isTrue(!TenantService.SYSTEM_TENANT_NAME.equals(tenant.name()), String.format("Tenant '%s' cannot be deleted", tenant.id()));
    }
}
