package com.kush.todo.mapper;

import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.async.AsyncOperationEventDto;
import com.kush.todo.dto.async.AsyncOperationStatus;
import com.kush.todo.dto.response.AsyncOperationQueuedResponseDto;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper
public abstract class AsyncOperationMapper {

    public abstract AsyncOperationQueuedResponseDto toAsyncOperationQueuedResponseDto(AsyncOperationDto<?> asyncOperationDto);

    public <T> AsyncOperationDto<T> toAsyncOperationDto(UUID tenantId) {
        return AsyncOperationDto.<T>builder()
                                .id(UUID.randomUUID())
                                .tenantId(tenantId)
                                .status(AsyncOperationStatus.IN_PROGRESS)
                                .build();
    }

    public <T> AsyncOperationDto<T> toSuccessAsyncOperation(AsyncOperationDto<T> current, T result) {
        return AsyncOperationDto.<T>builder()
                                .id(current.id())
                                .tenantId(current.tenantId())
                                .status(AsyncOperationStatus.SUCCESS)
                                .result(result)
                                .build();
    }

    public <T> AsyncOperationDto<T> toErrorAsyncOperation(AsyncOperationDto<T> asyncOperation) {
        return AsyncOperationDto.<T>builder()
                                .id(asyncOperation.id())
                                .tenantId(asyncOperation.tenantId())
                                .status(AsyncOperationStatus.ERROR)
                                .build();
    }

    public <T> AsyncOperationEventDto<T> toAsyncOperationEventDto(AsyncOperationDto<?> operation, T request) {
        return AsyncOperationEventDto.<T>builder()
                                     .operationId(operation.id())
                                     .tenantId(operation.tenantId())
                                     .request(request)
                                     .build();
    }
}
