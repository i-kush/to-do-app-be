package com.kush.todo.validator;

import com.kush.todo.entity.Tenant;
import com.kush.todo.service.TenantService;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TenantValidator {

    public static final String ERROR_MESSAGE_CANT_DELETE_TENANT = "Tenant cannot be deleted";

    public void validateTenantDeletion(Tenant tenant) {
        Assert.isTrue(!TenantService.SYSTEM_TENANT_NAME.equals(tenant.name()), ERROR_MESSAGE_CANT_DELETE_TENANT);
    }
}
