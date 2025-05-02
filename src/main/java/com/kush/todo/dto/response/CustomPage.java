package com.kush.todo.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CustomPage<T>(
        int totalPages,
        long totalElements,
        int totalItems,
        List<T> items
) {
}