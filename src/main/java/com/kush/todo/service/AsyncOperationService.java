package com.kush.todo.service;

import com.kush.todo.config.RedisConfig;
import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.response.AsyncOperationLaunchedResponseDto;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.AsyncOperationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncOperationService {

    @Qualifier(RedisConfig.CACHE_NAME_ASYNC_OPERATIONS)
    private final Cache cache;
    private final AsyncOperationMapper asyncOperationMapper;

    @SuppressWarnings("unchecked")
    public <T> AsyncOperationDto<T> get(UUID operationId, UUID tenantId) {
        String key = toKey(operationId, tenantId);
        log.info("Searching for async operation result for key {}, id {}", key, operationId);

        return Optional.ofNullable(cache.get(key, AsyncOperationDto.class))
                       .orElseThrow(() -> new NotFoundException(String.format("No operation with id '%s'", operationId)));
    }

    public <T> AsyncOperationLaunchedResponseDto launchOperation(UUID tenantId, T request, String topicName) {
        AsyncOperationDto<T> launchedOperation = asyncOperationMapper.toLaunchedAsyncOperation(tenantId);
        cache.put(toKey(launchedOperation.id(), tenantId), launchedOperation);

        return asyncOperationMapper.toAsyncOperationLaunchedResponse(launchedOperation);
    }

    public <T> void executeLaunchedOperation(UUID operationId, UUID tenantId, Supplier<T> operation) {
        String key = toKey(operationId, tenantId);
        log.info("Executing launched async operation for key {}", key);

        AsyncOperationDto<T> asyncOperation = get(operationId, tenantId);
        try {
            asyncOperation = asyncOperationMapper.toSuccessAsyncOperation(asyncOperation, operation.get());
        } catch (RuntimeException e) {
            log.error("Error executing async operation for key {}", key, e);
            asyncOperation = asyncOperationMapper.toErrorAsyncOperation(asyncOperation, e);
        }
        cache.put(key, asyncOperation);
    }

    private String toKey(UUID operationId, UUID tenantId) {
        return String.format("%s_%s", tenantId, operationId);
    }
}
