package com.kush.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kush.todo.dto.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AppUserResponseDto(
        UUID id,
        UUID tenantId,
        Role roleId,
        String username,
        String email,
        String firstname,
        String lastname,
        @JsonIgnore
        String passwordHash,
        @JsonIgnore
        Integer loginAttempts,
        @JsonIgnore
        Instant lastLoginAttemptAt,
        boolean isLocked,
        @JsonIgnore
        Instant lockedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
