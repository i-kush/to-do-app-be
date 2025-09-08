package com.kush.todo.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ProjectResponseDto(
        UUID id,
        UUID tenantId,
        String name,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}