package com.kush.todo.converter;

import com.kush.todo.BaseTest;
import com.kush.todo.dto.ProjectStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

class ProjectStatusCaseInsensitivePathVariableConverterTest extends BaseTest {

    private final ProjectStatusCaseInsensitiveConverter converter = new ProjectStatusCaseInsensitiveConverter();

    @ParameterizedTest
    @EnumSource(ProjectStatus.class)
    void convert(ProjectStatus expected) {
        ProjectStatus actual = converter.convert(expected.name().toLowerCase(Locale.getDefault()));
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "   "})
    @NullAndEmptySource
    void convertEmpty(String invalidInput) {
        ProjectStatus actual = converter.convert(invalidInput);
        Assertions.assertNull(actual);
    }

}