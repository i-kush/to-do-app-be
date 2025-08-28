package com.kush.todo.entity;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;

@Builder
public record Tenant(
        @Id
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
}
