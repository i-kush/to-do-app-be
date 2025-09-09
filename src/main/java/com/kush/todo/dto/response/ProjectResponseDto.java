package com.kush.todo.dto.response;

import com.kush.todo.dto.ProjectStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record ProjectResponseDto(
        UUID id,
        UUID tenantId,
        String name,
        String description,
        ProjectStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}