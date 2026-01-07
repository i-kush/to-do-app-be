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

class JsonNodeWriteConverterTest extends BaseTest {

    private final JsonNodeWriteConverter jsonNodeWriteConverter = new JsonNodeWriteConverter(TestDataBuilder.OBJECT_MAPPER);

    @ParameterizedTest
    @MethodSource("getConvertParams")
    void convert(JsonNode source, PGobject expected) {
        PGobject actual = Assertions.assertDoesNotThrow(() -> jsonNodeWriteConverter.convert(source));
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> getConvertParams() {
        return Stream.of(
                Arguments.of(null, TestDataBuilder.newPgObject(null)),
                Arguments.of(TestDataBuilder.OBJECT_MAPPER.createObjectNode(), TestDataBuilder.newPgObject("{}"))
        );
    }
}