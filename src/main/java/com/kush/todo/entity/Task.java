package com.kush.todo.entity;

import com.kush.todo.dto.TaskStatus;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Builder
@Table
public record Task(
        @Id
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