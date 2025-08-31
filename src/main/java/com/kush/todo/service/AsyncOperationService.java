package com.kush.todo.service;

import com.kush.todo.config.RedisConfig;
import com.kush.todo.dto.AsyncOperationStatus;
import com.kush.todo.dto.CurrentUser;
import com.kush.todo.dto.response.AsyncOperationLaunchedResponseDto;
import com.kush.todo.dto.response.AsyncOperationResultResponseDto;
import com.kush.todo.exception.NotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncOperationService {

    @Qualifier(RedisConfig.CACHE_NAME_ASYNC_OPERATIONS)
    private final Cache cache;
    private final CurrentUser currentUser;

    @SuppressWarnings("unchecked")
    public <T> AsyncOperationResultResponseDto<T> get(@NotNull UUID operationId) {
        String key = toKey(operationId);
        log.info("Searching for async operation result for key {}, id {}", key, operationId);

        return Optional.ofNullable(cache.get(key, AsyncOperationResultResponseDto.class))
                       .orElseThrow(() -> new NotFoundException(String.format("No operation with id '%s'", operationId)));
    }

    public AsyncOperationLaunchedResponseDto launch(Object request, String topicName) {
        UUID operationId = UUID.randomUUID();
        AsyncOperationResultResponseDto<?> initial = AsyncOperationResultResponseDto.builder()
                                                                                    .id(operationId)
                                                                                    .status(AsyncOperationStatus.IN_PROGRESS)
                                                                                    .build();
        cache.put(toKey(operationId), initial);
        return AsyncOperationLaunchedResponseDto.builder()
                                                .id(operationId)
                                                .build();
    }

    private String toKey(UUID operationId) {
        return String.format("%s_%s", currentUser.getTenantId(), operationId);
    }
}
