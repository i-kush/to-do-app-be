package com.kush.todo.service;

import com.kush.todo.config.RedisConfig;
import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.async.AsyncOperationEventDto;
import com.kush.todo.dto.response.AsyncOperationQueuedResponseDto;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.AsyncOperationMapper;
import com.kush.todo.producer.KafkaProducer;
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
    private final KafkaProducer kafkaProducer;

    public <T> AsyncOperationDto<T> getOperation(UUID operationId, UUID tenantId) {
        String key = toKey(operationId, tenantId);
        log.info("Searching for async operation result for key {}, id {}", key, operationId);

        return getOperation(key);
    }

    @SuppressWarnings("unchecked")
    private <T> AsyncOperationDto<T> getOperation(String key) {
        return Optional.ofNullable(cache.get(key, AsyncOperationDto.class))
                       .orElseThrow(() -> new NotFoundException("No such operation"));
    }

    public <T> AsyncOperationQueuedResponseDto queueOperation(UUID tenantId, T request, String topicName) {
        AsyncOperationDto<?> operation = asyncOperationMapper.toAsyncOperationDto(tenantId);
        String key = toKey(operation.id(), tenantId);
        cache.put(key, operation);

        log.info("Queueing async operation result for key {}, id {}", key, operation.id());

        kafkaProducer.send(asyncOperationMapper.toAsyncOperationEventDto(operation, request), topicName);

        return asyncOperationMapper.toAsyncOperationQueuedResponseDto(operation);
    }

    public <T> void executeOperation(AsyncOperationEventDto<?> operation, Supplier<T> operationSupplier) {
        String key = toKey(operation.operationId(), operation.tenantId());
        log.info("Processing launched async operation for key {}", key);

        AsyncOperationDto<T> asyncOperation = getOperation(key);
        try {
            asyncOperation = asyncOperationMapper.toSuccessAsyncOperation(asyncOperation, operationSupplier.get());
        } catch (RuntimeException e) {
            log.error("Error executing async operation for key {}", key, e);
            asyncOperation = asyncOperationMapper.toErrorAsyncOperation(asyncOperation);
        }
        cache.put(key, asyncOperation);
    }

    private String toKey(UUID operationId, UUID tenantId) {
        return String.format("%s_%s", tenantId, operationId);
    }
}
