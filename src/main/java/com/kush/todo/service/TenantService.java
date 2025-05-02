package com.kush.todo.service;

import com.kush.todo.dto.response.TenantDto;
import com.kush.todo.entity.Tenant;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.TenantMapper;
import com.kush.todo.repository.TenantRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    public TenantDto create(TenantDto tenantDto) {
        Assert.notNull(tenantDto, "'tenantDto' must not be null");

        Tenant tenant = tenantMapper.toTenant(tenantDto);
        Tenant createdTenant = tenantRepository.save(tenant);
        return tenantMapper.toTenantDto(createdTenant);
    }

    public TenantDto findById(UUID id) {
        Assert.notNull(id, "'id' must not be null");
        Tenant tenant = tenantRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("No such id " + id));
        return tenantMapper.toTenantDto(tenant);
    }

    public TenantDto update(TenantDto tenantDto) {
        //ToDo
        return null;
    }
}
