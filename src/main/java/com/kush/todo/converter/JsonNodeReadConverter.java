package com.kush.todo.converter;

import lombok.RequiredArgsConstructor;
import org.postgresql.util.PGobject;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.StringUtils;

@ReadingConverter
@RequiredArgsConstructor
public class JsonNodeReadConverter implements Converter<PGobject, JsonNode> {

    private final ObjectMapper objectMapper;

    @Override
    public JsonNode convert(PGobject source) {
        return StringUtils.hasText(source.getValue()) ? objectMapper.readTree(source.getValue()) : null;
    }
}