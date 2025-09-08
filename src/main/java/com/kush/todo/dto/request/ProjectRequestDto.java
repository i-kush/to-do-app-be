package com.kush.todo.dto.request;

import com.kush.todo.dto.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProjectRequestDto(
        @NotBlank
        @Size(min = 1, max = 50)
        String name,
        @NotBlank
        @Size(min = 1, max = 100)
        String description,
        @NotNull
        ProjectStatus status
) {
}

