package com.kush.todo.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record TenantResponseDto(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {

}
