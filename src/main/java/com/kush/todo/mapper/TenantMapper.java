package com.kush.todo.mapper;

import com.kush.todo.constant.MappingConstants;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.request.UpdateTenantRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.TenantDeleteResponseDto;
import com.kush.todo.dto.response.TenantDetailsResponseDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.UUID;

@Mapper
public abstract class TenantMapper extends PageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Tenant toTenant(UpdateTenantRequestDto tenantDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Tenant toTenant(CreateTenantRequestDto tenantDto);

    @Mapping(target = "id", source = "tenant.id")
    @Mapping(target = "name", source = "tenantDto.name")
    @Mapping(target = "createdAt", source = "tenant.createdAt")
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Tenant toTenant(Tenant tenant, UpdateTenantRequestDto tenantDto);

    public abstract TenantResponseDto toTenantDto(Tenant tenant);

    public abstract TenantDetailsResponseDto toTenantDetailsResponseDto(TenantResponseDto tenant,
                                                                        Set<AppUserResponseDto> admins);

    public abstract TenantDeleteResponseDto toTenantDeleteResponseDto(int usersDeleted, UUID tenantId);
}
