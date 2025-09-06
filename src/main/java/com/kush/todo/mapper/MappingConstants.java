package com.kush.todo.mapper;

public final class MappingConstants {

    public static final String EXPRESSION_TIMESTAMP_NOW = "java(java.time.Instant.now())";
    public static final String EXPRESSION_PASSWORD_HASH = "java(passwordEncoder.encode(appUserRequestDto.password()))";
    public static final String EXPRESSION_MDC_SPAN_ID = "java(org.slf4j.MDC.get(\"spanId\"))";
    public static final String EXPRESSION_MDC_TRACE_ID = "java(org.slf4j.MDC.get(\"traceId\"))";

    private MappingConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
