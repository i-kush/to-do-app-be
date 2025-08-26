package com.kush.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AppUserResponseDto(
        UUID id,
        UUID tenantId,
        String username,
        String email,
        String firstname,
        String lastname,
        @JsonIgnore
        String passwordHash,
        boolean isLocked,
        boolean isActivated,
        Instant created,
        Instant updated
) {
}
