package com.kush.todo.mapper;

public final class MappingConstants {

    public static final String EXPRESSION_TIMESTAMP_NOW = "java(java.time.Instant.now())";
    public static final String EXPRESSION_PASSWORD_HASH = "java(passwordEncoder.encode(appUserRequestDto.password()))";

    private MappingConstants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
