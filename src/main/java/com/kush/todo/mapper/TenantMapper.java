package com.kush.todo.mapper;

import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class TenantMapper extends PageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    @Mapping(target = "updated", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Tenant toTenant(TenantRequestDto tenantDto);

    @Mapping(target = "id", source = "tenant.id")
    @Mapping(target = "name", source = "tenantDto.name")
    @Mapping(target = "created", source = "tenant.created")
    @Mapping(target = "updated", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Tenant toTenant(Tenant tenant, TenantRequestDto tenantDto);

    public abstract TenantResponseDto toTenantDto(Tenant tenant);
}
