package com.kush.todo.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.kush.todo.BaseTest;
import com.kush.todo.TestDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.postgresql.util.PGobject;

import java.util.UUID;
import java.util.stream.Stream;

class JsonNodeReadConverterTest extends BaseTest {

    private final JsonNodeReadConverter jsonNodeReadConverter = new JsonNodeReadConverter(TestDataBuilder.OBJECT_MAPPER);

    @Test
    void convertError() {
        String invalidSource = UUID.randomUUID().toString();
        IllegalArgumentException actual = Assertions.assertThrows(IllegalArgumentException.class,
                                                                  () -> jsonNodeReadConverter.convert(TestDataBuilder.newPgObject(invalidSource)));
        Assertions.assertEquals(String.format(JsonNodeReadConverter.ERROR_MESSAGE_PATTERN, invalidSource), actual.getMessage());
    }

    @ParameterizedTest
    @MethodSource("getConvertParams")
    void convert(PGobject source, JsonNode expected) {
        JsonNode actual = Assertions.assertDoesNotThrow(() -> jsonNodeReadConverter.convert(source));
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> getConvertParams() {
        return Stream.of(
                Arguments.of(TestDataBuilder.newPgObject(""), null),
                Arguments.of(TestDataBuilder.newPgObject("{}"), TestDataBuilder.OBJECT_MAPPER.createObjectNode())
        );
    }
}