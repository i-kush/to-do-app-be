package com.kush.todo.dto.async;

import java.util.UUID;

public record AsyncOperationEventDto<T>(
        UUID operationId,
        UUID tenantId,
        T request
) {
}
