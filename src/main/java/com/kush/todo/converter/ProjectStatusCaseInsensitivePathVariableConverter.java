package com.kush.todo.converter;

import com.kush.todo.dto.ProjectStatus;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProjectStatusCaseInsensitivePathVariableConverter implements Converter<String, ProjectStatus> {

    @Override
    public ProjectStatus convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }
        return ProjectStatus.valueOf(source.toUpperCase());
    }
}
