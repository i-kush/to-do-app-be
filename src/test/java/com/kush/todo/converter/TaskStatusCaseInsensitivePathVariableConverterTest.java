package com.kush.todo.converter;

import com.kush.todo.BaseTest;
import com.kush.todo.dto.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

class TaskStatusCaseInsensitivePathVariableConverterTest extends BaseTest {

    private final TaskStatusCaseInsensitiveConverter converter = new TaskStatusCaseInsensitiveConverter();

    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    void convert(TaskStatus expected) {
        TaskStatus actual = converter.convert(expected.name().toLowerCase(Locale.getDefault()));
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   "})
    @NullAndEmptySource
    void convertEmpty(String invalidInput) {
        TaskStatus actual = converter.convert(invalidInput);
        Assertions.assertNull(actual);
    }

}