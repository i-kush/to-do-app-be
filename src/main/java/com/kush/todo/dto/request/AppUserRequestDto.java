package com.kush.todo.dto.request;

import com.kush.todo.dto.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AppUserRequestDto(
        @NotBlank
        @Size(min = 1, max = 20)
        String username,
        @NotBlank
        @Size(min = 1, max = 20)
        String password,
        @NotNull
        Role roleId,
        @NotBlank
        @Size(min = 1, max = 50)
        @Email
        String email,
        @NotBlank
        @Size(min = 1, max = 50)
        String firstname,
        @NotBlank
        @Size(min = 1, max = 50)
        String lastname
) {
}
