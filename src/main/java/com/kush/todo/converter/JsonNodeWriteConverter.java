package com.kush.todo.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
@RequiredArgsConstructor
public class JsonNodeWriteConverter implements Converter<JsonNode, PGobject> {

    public static final String TYPE_JSON_B = "jsonb";

    private final ObjectMapper objectMapper;

    @Override
    public PGobject convert(JsonNode source) {
        try {
            PGobject result = new PGobject();
            result.setType(TYPE_JSON_B);
            result.setValue(source == null ? null : objectMapper.writeValueAsString(source));

            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to write JSON '%s'", source), e);
        } catch (SQLException e) {
            throw new IllegalStateException(String.format("Error during SQL 'jsonb' value setting for value %s", source), e);
        }
    }
}