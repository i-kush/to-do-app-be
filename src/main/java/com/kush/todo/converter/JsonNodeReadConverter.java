package com.kush.todo.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;

import java.io.IOException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.StringUtils;

@ReadingConverter
@RequiredArgsConstructor
public class JsonNodeReadConverter implements Converter<PGobject, JsonNode> {

    private final ObjectMapper objectMapper;

    @Override
    public JsonNode convert(PGobject source) {
        try {
            return StringUtils.hasText(source.getValue()) ? objectMapper.readTree(source.getValue()) : null;
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Failed to read JSON '%s'", source), e);
        }
    }
}