package com.kush.todo.dto.async;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AsyncOperationEventDto<T>(
        UUID operationId,
        UUID tenantId,
        T request
) {
}
