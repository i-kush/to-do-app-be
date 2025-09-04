package com.kush.todo.facade;

import com.kush.todo.config.KafkaTopics;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.AsyncOperationQueuedResponseDto;
import com.kush.todo.dto.response.TenantDeleteResponseDto;
import com.kush.todo.dto.response.TenantDetailsResponseDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.mapper.TenantMapper;
import com.kush.todo.service.AppUserService;
import com.kush.todo.service.AsyncOperationService;
import com.kush.todo.service.TenantService;
import com.kush.todo.validator.TenantValidator;
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
    public TenantDeleteResponseDto delete(UUID id) {
        log.warn("Starting tenant {} offboarding", id);
        if (tenantService.isSystemTenant(id)) {
            throw new IllegalArgumentException(TenantValidator.ERROR_MESSAGE_CANT_DELETE_TENANT);
        }

        int usersDeletedCount = appUserService.deleteByTenantId(id);
        tenantService.delete(id);
        log.info("Successfully finished tenant '{}'", id);

        return tenantMapper.toTenantDeleteResponseDto(usersDeletedCount, id);
    }

    public AsyncOperationQueuedResponseDto createAsync(CreateTenantRequestDto tenantRequestDto) {
        return asyncOperationService.queueOperation(currentUser.getTenantId(), tenantRequestDto, kafkaTopics.onboardTenant());
    }

    public AsyncOperationQueuedResponseDto deleteAsync(UUID id) {
        return asyncOperationService.queueOperation(currentUser.getTenantId(), id, kafkaTopics.offboardTenant());
    }
}
