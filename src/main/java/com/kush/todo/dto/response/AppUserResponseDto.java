package com.kush.todo.dto.response;

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
        boolean isLocked,
        boolean isActivated,
        Instant created,
        Instant updated
) {
}
