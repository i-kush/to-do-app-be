package com.kush.todo.dto.response;

import java.util.UUID;

public record TenantDto(
        UUID id,
        String name
) {

}
