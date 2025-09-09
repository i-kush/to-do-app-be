package com.kush.todo.converter;

import lombok.RequiredArgsConstructor;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public abstract class CaseInsensitiveEnumConverter<T extends Enum<T>> implements Converter<String, T> {

    private final Class<T> enumType;

    @Override
    public T convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }

        return Enum.valueOf(enumType, source.trim().toUpperCase(Locale.getDefault()));
    }
}
