package com.kush.todo.service;

import com.kush.todo.dto.response.AsyncOperationLaunchedResponseDto;
import com.kush.todo.dto.response.AsyncOperationResultResponseDto;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class AsyncOperationService {

    public <T> AsyncOperationResultResponseDto<T> get(@NotNull UUID id) {
        return AsyncOperationResultResponseDto.<T>builder()
                                              .id(id)
                                              .build();
    }

    public AsyncOperationLaunchedResponseDto launch(Object request) {
        return AsyncOperationLaunchedResponseDto.builder()
                                                .id(UUID.randomUUID())
                                                .build();
    }
}
