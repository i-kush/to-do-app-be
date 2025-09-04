package com.kush.todo.facade;

import com.kush.todo.config.KafkaTopics;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.AsyncOperationQueuedResponseDto;
import com.kush.todo.dto.response.TenantDetailsResponseDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.mapper.TenantMapper;
import com.kush.todo.service.AppUserService;
import com.kush.todo.service.AsyncOperationService;
import com.kush.todo.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantFacade {

    private final TenantService tenantService;
    private final TenantMapper tenantMapper;
    private final AppUserService appUserService;
    private final AsyncOperationService asyncOperationService;
    private final CurrentUser currentUser;
    private final KafkaTopics kafkaTopics;

    @Transactional
    public TenantDetailsResponseDto create(CreateTenantRequestDto request) {
        log.info("Starting tenant {} onboarding", request.name());
        TenantResponseDto tenantResponseDto = tenantService.create(request);
        AppUserResponseDto appUserResponseDto = appUserService.createFirstAdmin(tenantResponseDto.id(), request.adminEmail());
        log.info("Successfully finished tenant '{}' onboarding - tenant={}, admin={}", request.name(), tenantResponseDto.id(), appUserResponseDto.id());

        return tenantMapper.toTenantDetailsResponseDto(tenantResponseDto, Collections.singleton(appUserResponseDto));
    }

    @Transactional
    public boolean delete(UUID id) {
        //ToDo implement as part of https://github.com/i-kush/to-do-app-be/issues/34
        tenantService.delete(id);
        return false;
    }

    public AsyncOperationQueuedResponseDto createAsync(CreateTenantRequestDto tenantRequestDto) {
        return asyncOperationService.queueOperation(currentUser.getTenantId(), tenantRequestDto, kafkaTopics.onboardTenant());
    }

    public AsyncOperationQueuedResponseDto deleteAsync(UUID id) {
        return asyncOperationService.queueOperation(currentUser.getTenantId(), id, kafkaTopics.offboardTenant());
    }
}
