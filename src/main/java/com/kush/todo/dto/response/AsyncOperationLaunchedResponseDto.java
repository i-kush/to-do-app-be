package com.kush.todo.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AsyncOperationLaunchedResponseDto(UUID id) {
}
