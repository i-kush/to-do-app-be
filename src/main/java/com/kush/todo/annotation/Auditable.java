package com.kush.todo.annotation;

import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    AuditActionType actionType();
    AuditTargetType targetType();
}
