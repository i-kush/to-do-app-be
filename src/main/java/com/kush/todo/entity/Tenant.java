package com.kush.todo.entity;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Builder
public record Tenant(
        @Id
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
