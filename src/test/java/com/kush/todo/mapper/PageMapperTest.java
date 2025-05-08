package com.kush.todo.mapper;

import com.kush.todo.TestDataBuilder;
import com.kush.todo.dto.response.CustomPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;

class PageMapperTest {

    private final PageMapper pageMapper = new PageMapper() {
    };

    @Test
    void toCustomPage() {
        int size = 10;
        Page<Object> page = TestDataBuilder.buildPage(Object::new, size);
        CustomPage<Object> customPage = pageMapper.toCustomPage(page);

        Assertions.assertNotNull(customPage);
        Assertions.assertEquals(size, page.getSize());
        Assertions.assertEquals(page.getTotalPages(), customPage.totalPages());
        Assertions.assertEquals(page.getTotalElements(), customPage.totalElements());
        Assertions.assertEquals(page.getNumberOfElements(), customPage.totalItems());
        Assertions.assertIterableEquals(page.getContent(), customPage.items());
    }
}