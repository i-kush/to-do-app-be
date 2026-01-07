package com.kush.todo.converter;

import com.kush.todo.BaseTest;
import com.kush.todo.TestDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.postgresql.util.PGobject;
import tools.jackson.databind.JsonNode;

import java.util.stream.Stream;

class JsonNodeReadConverterTest extends BaseTest {

    private final JsonNodeReadConverter jsonNodeReadConverter = new JsonNodeReadConverter(TestDataBuilder.OBJECT_MAPPER);

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