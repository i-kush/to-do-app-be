package com.kush.todo.dto.response;

import java.util.UUID;

public record TenantResponseDto(
        UUID id,
        String name
) {

}
