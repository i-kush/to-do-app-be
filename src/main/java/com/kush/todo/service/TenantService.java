package com.kush.todo.service;

import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.entity.Tenant;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.TenantMapper;
import com.kush.todo.repository.TenantRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Transactional
    public TenantResponseDto create(TenantRequestDto tenantDto) {
        Tenant tenant = tenantMapper.toTenant(tenantDto);
        Tenant createdTenant = tenantRepository.save(tenant);

        return tenantMapper.toTenantDto(createdTenant);
    }

    @Transactional(readOnly = true)
    public TenantResponseDto findByIdRequired(UUID id) {
        return tenantMapper.toTenantDto(getRequired(id));
    }

    @Transactional
    public TenantResponseDto update(UUID id, TenantRequestDto tenantDto) {
        Tenant tenant = tenantMapper.toTenant(getRequired(id), tenantDto);
        Tenant udpatedTenant = tenantRepository.save(tenant);

        return tenantMapper.toTenantDto(udpatedTenant);
    }

    @Transactional
    public void delete(UUID id) {
        if (!tenantRepository.existsById(id)) {
            throw new NotFoundException(String.format("No tenant with id '%s'", id));
        }
        tenantRepository.deleteById(id);
    }

    private Tenant getRequired(UUID id) {
        return tenantRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No tenant with id '%s'", id)));
    }

    @Transactional(readOnly = true)
    public CustomPage<TenantResponseDto> findAll(int page, int size) {
        Page<TenantResponseDto> pages = tenantRepository.findAll(PageRequest.of(page - 1, size))
                                                        .map(tenantMapper::toTenantDto);
        return tenantMapper.toCustomPage(pages);
    }
}