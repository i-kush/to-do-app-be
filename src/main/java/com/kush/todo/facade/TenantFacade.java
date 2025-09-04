package com.kush.todo.facade;

import com.kush.todo.config.KafkaTopics;
import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.request.UpdateTenantRequestDto;
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
        TenantResponseDto tenantResponseDto = tenantService.create(request);
        AppUserResponseDto appUserResponseDto = appUserService.createFirstAdmin(tenantResponseDto.id(), request.adminEmail());

        return tenantMapper.toTenantDetailsResponseDto(tenantResponseDto, Collections.singleton(appUserResponseDto));
    }

    public AsyncOperationDto<TenantResponseDto> getAsyncResult(UUID id) {
        return asyncOperationService.getOperation(id, currentUser.getTenantId());
    }

    public AsyncOperationQueuedResponseDto createAsync(CreateTenantRequestDto tenantRequestDto) {
        return asyncOperationService.queueOperation(currentUser.getTenantId(), tenantRequestDto, kafkaTopics.onboardTenant());
    }
}
