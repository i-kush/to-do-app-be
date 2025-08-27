package com.kush.todo.entity;

import com.kush.todo.dto.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table
@Builder
public record AppUser(
        @Id
        UUID id,
        UUID tenantId,
        Role roleId,
        String username,
        String passwordHash,
        String email,
        String firstname,
        String lastname,
        boolean isLocked,
        Instant lockedAt,
        Instant createdAt,
        Instant updatedAt
) {
}