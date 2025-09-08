package com.kush.todo.entity;

import com.kush.todo.dto.ProjectStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table
public record Project(
        @Id
        UUID id,
        UUID tenantId,
        String name,
        String description,
        ProjectStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}