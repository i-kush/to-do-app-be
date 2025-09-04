package com.kush.todo.dto.response;

import lombok.Builder;

import java.util.Set;

@Builder
public record TenantDetailsResponseDto(
        TenantResponseDto tenant,
        Set<AppUserResponseDto> admins
) {
}
