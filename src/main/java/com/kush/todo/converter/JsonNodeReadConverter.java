package com.kush.todo.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.StringUtils;

@ReadingConverter
@RequiredArgsConstructor
public class JsonNodeReadConverter implements Converter<PGobject, JsonNode> {

    public static final String ERROR_MESSAGE_PATTERN = "Failed to read JSON '%s'";

    private final ObjectMapper objectMapper;

    @Override
    public JsonNode convert(PGobject source) {
        try {
            return StringUtils.hasText(source.getValue()) ? objectMapper.readTree(source.getValue()) : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE_PATTERN, source), e);
        }
    }
}