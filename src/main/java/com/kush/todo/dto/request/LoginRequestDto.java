package com.kush.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank
        @Size(min = 1, max = 50)
        String username,

        @NotBlank
        @Size(min = 1, max = 50)
        String password
) {
}
