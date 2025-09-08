package com.kush.todo.constant;

public final class CommonErrorMessages {

    public static final String PATTERN_NOT_FOUND = "Not found id '%s'";
    public static final String USER_LOCKED = "User is locked";
    public static final String USER_INVALID_CREDS = "Invalid username or password";

    private CommonErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
