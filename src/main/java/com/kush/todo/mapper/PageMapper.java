package com.kush.todo.mapper;

import com.kush.todo.dto.response.CustomPage;

import org.springframework.data.domain.Page;

public abstract class PageMapper {

    public <T> CustomPage<T> toCustomPage(Page<T> page) {
        return CustomPage.<T>builder()
                         .totalPages(page.getTotalPages())
                         .totalElements(page.getTotalElements())
                         .totalItems(page.getNumberOfElements())
                         .items(page.getContent())
                         .build();
    }
}
