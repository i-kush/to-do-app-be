package com.kush.todo.facade;

import com.kush.todo.config.KafkaTopics;
import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.AsyncOperationQueuedResponseDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.service.AsyncOperationService;
import com.kush.todo.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantFacade {

    private final TenantService tenantService;
    private final AsyncOperationService asyncOperationService;
    private final CurrentUser currentUser;
    private final KafkaTopics kafkaTopics;

    @Transactional
    public TenantResponseDto create(TenantRequestDto request) {
        //ToDo https://github.com/i-kush/to-do-app-be/issues/23 introduce onboarding
        return tenantService.create(request);
    }

    public AsyncOperationDto<TenantResponseDto> getAsyncResult(UUID id) {
        return asyncOperationService.getOperation(id, currentUser.getTenantId());
    }

    public AsyncOperationQueuedResponseDto createAsync(TenantRequestDto tenantRequestDto) {
        return asyncOperationService.queueOperation(currentUser.getTenantId(), tenantRequestDto, kafkaTopics.onboardTenant());
    }
}
