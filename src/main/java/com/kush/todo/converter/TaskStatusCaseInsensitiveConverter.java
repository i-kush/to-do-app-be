package com.kush.todo.converter;

import com.kush.todo.dto.TaskStatus;

import org.springframework.stereotype.Component;

@Component
public class TaskStatusCaseInsensitiveConverter extends CaseInsensitiveEnumConverter<TaskStatus> {

    public TaskStatusCaseInsensitiveConverter() {
        super(TaskStatus.class);
    }
}
