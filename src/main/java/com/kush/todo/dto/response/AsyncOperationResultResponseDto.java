package com.kush.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kush.todo.dto.AsyncOperationStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AsyncOperationResultResponseDto<T>(
        UUID id,
        AsyncOperationStatus status,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T result
) {
}