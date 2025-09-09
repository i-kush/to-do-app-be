package com.kush.todo.converter;

import com.kush.todo.dto.TaskStatus;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TaskStatusCaseInsensitivePathVariableConverter implements Converter<String, TaskStatus> {

    @Override
    public TaskStatus convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }
        return TaskStatus.valueOf(source.toUpperCase());
    }
}
