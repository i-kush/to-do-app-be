package com.kush.todo.mapper;

import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.async.AsyncOperationStatus;
import com.kush.todo.dto.response.AsyncOperationLaunchedResponseDto;
import org.mapstruct.Mapper;

import java.util.UUID;

import org.springframework.util.StringUtils;

@Mapper
public abstract class AsyncOperationMapper {

    public abstract AsyncOperationLaunchedResponseDto toAsyncOperationLaunchedResponse(AsyncOperationDto<?> asyncOperationDto);

    public <T> AsyncOperationDto<T> toLaunchedAsyncOperation(UUID tenantId) {
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

    public <T> AsyncOperationDto<T> toErrorAsyncOperation(AsyncOperationDto<T> asyncOperation, Exception e) {
        return AsyncOperationDto.<T>builder()
                                .id(asyncOperation.id())
                                .tenantId(asyncOperation.tenantId())
                                .status(AsyncOperationStatus.ERROR)
                                .error(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "Unknown Error")
                                .build();
    }
}
