package com.kush.todo.dto.response;

import com.kush.todo.dto.TaskStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record TaskResponseDto(
        UUID id,
        UUID tenantId,
        UUID projectId,
        String name,
        String description,
        UUID assignedUserId,
        TaskStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}