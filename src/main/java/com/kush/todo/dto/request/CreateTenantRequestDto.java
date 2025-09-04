package com.kush.todo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateTenantRequestDto(
        @NotBlank
        @Size(min = 1, max = 50)
        String name,

        @NotBlank
        @Size(min = 1, max = 50)
        @Email
        String adminEmail
) {

}
