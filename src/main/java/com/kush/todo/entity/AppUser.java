package com.kush.todo.entity;

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
        String username,
        String passwordHash,
        String email,
        String firstname,
        String lastname,
        boolean isLocked,
        boolean isActivated,
        Instant created,
        Instant updated
) {
}