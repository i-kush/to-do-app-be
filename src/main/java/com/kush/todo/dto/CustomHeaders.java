package com.kush.todo.dto;

public final class CustomHeaders {

    public static final String REQUEST_ID = "X-ToDo-Request-Id";

    private CustomHeaders() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
