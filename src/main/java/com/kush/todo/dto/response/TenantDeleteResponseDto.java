package com.kush.todo.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TenantDeleteResponseDto(
        UUID tenantId,
        int usersDeleted
) {
}
