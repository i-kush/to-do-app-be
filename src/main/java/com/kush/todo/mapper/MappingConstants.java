package com.kush.todo.mapper;

public final class MappingConstants {

    public static final String TIMESTAMP_NOW_EXPRESSION = "java(java.time.Instant.now())";

    public MappingConstants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }
}
