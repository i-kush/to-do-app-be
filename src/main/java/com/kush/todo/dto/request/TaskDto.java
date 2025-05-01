package com.kush.todo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record TaskDto(
        @NotEmpty @Size(max = 20) String name
) {

}
