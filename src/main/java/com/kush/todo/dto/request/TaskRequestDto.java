package com.kush.todo.dto.request;

import com.kush.todo.dto.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record TaskRequestDto(
        @NotBlank
        @Size(min = 1, max = 100)
        String name,
        @NotBlank
        @Size(min = 1, max = 200)
        String description,
        UUID assignedUserId,
        TaskStatus status
) {
}