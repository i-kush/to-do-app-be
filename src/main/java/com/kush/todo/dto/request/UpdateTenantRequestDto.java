package com.kush.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateTenantRequestDto(
        @NotBlank
        @Size(min = 1, max = 50)
        String name
) {

}
