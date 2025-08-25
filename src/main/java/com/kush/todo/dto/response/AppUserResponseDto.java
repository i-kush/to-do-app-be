package com.kush.todo.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AppUserResponseDto(
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
