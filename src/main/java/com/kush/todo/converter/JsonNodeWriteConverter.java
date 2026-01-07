package com.kush.todo.converter;

import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.sql.SQLException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
@RequiredArgsConstructor
public class JsonNodeWriteConverter implements Converter<JsonNode, PGobject> {

    public static final String ERROR_MESSAGE_PATTERN_JSON = "Failed to write JSON '%s'";
    public static final String TYPE_JSON_B = "jsonb";

    private final ObjectMapper objectMapper;

    @Override
    public PGobject convert(JsonNode source) {
        try {
            PGobject result = new PGobject();
            result.setType(TYPE_JSON_B);
            result.setValue(source == null ? null : objectMapper.writeValueAsString(source));

            return result;
        } catch (SQLException e) {
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE_PATTERN_JSON, source), e);
        }
    }
}