package com.kush.todo.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.todo.BaseTest;
import com.kush.todo.TestDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.postgresql.util.PGobject;

import java.util.stream.Stream;

class JsonNodeWriteConverterTest extends BaseTest {

    private final JsonNodeWriteConverter jsonNodeWriteConverter = new JsonNodeWriteConverter(TestDataBuilder.OBJECT_MAPPER);

    @Test
    void convertJsonError() throws JsonProcessingException {
        ObjectMapper objectMapper = Mockito.mock(ObjectMapper.class);
        JsonProcessingException exception = Mockito.mock(JsonProcessingException.class);

        JsonNode invalidSource = TestDataBuilder.OBJECT_MAPPER.createObjectNode();
        JsonNodeWriteConverter subject = new JsonNodeWriteConverter(objectMapper);

        Mockito.when(objectMapper.writeValueAsString(invalidSource)).thenThrow(exception);

        IllegalArgumentException actual = Assertions.assertThrows(IllegalArgumentException.class,
                                                                  () -> subject.convert(invalidSource));
        Assertions.assertEquals(String.format(JsonNodeWriteConverter.ERROR_MESSAGE_PATTERN_JSON, invalidSource), actual.getMessage());
    }

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