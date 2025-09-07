package com.kush.todo.constant;

public final class MetricsConstants {

    public static final String TIMER_ENDPOINT = "todo.controller.endpoint.time";
    public static final String COUNT_ENDPOINT_ERROR = "todo.controller.endpoint.error.count";
    public static final String TAG_DOMAIN_NAME = "domain";
    public static final String TAG_OPERATION_NAME = "operation";
    public static final String TAG_DOMAIN_USER = "user";
    public static final String TAG_DOMAIN_TENANT = "tenant";
    public static final String TAG_DOMAIN_PROJECT = "project";
    public static final String TAG_DOMAIN_TASK = "task";
    public static final String TAG_DOMAIN_OPERATION = "operation";
    public static final String TAG_OPERATION_CREATE = "create";
    public static final String TAG_OPERATION_READ = "read";
    public static final String TAG_OPERATION_UPDATE = "update";
    public static final String TAG_OPERATION_DELETE = "delete";

    private MetricsConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
