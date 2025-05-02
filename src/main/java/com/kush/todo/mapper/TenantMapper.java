package com.kush.todo.mapper;

import com.kush.todo.dto.response.TenantDto;
import com.kush.todo.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class TenantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    public abstract Tenant toTenant(TenantDto tenantDto);

    public abstract TenantDto toTenantDto(Tenant tenant);
}
