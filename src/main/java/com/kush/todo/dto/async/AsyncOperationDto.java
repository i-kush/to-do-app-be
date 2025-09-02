package com.kush.todo.dto.async;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AsyncOperationDto<T>(
        UUID id,
        UUID tenantId,
        AsyncOperationStatus status,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T result
) {
}