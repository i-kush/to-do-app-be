package com.kush.todo.entity;

import com.kush.todo.dto.common.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table
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
        Integer loginAttempts,
        Instant lastLoginAttemptAt,
        boolean isLocked,
        Instant lockedAt,
        Instant createdAt,
        Instant updatedAt
) {
}