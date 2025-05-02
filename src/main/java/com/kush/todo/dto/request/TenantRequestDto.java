package com.kush.todo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TenantRequestDto(
        @NotEmpty
        @Size(min = 1, max = 50)
        String name
) {

}
