package com.kush.todo.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TenantResponseDto(
        UUID id,

        String name
) {

}
