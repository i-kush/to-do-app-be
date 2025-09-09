package com.kush.todo.converter;

import com.kush.todo.dto.ProjectStatus;

import org.springframework.stereotype.Component;

@Component
public class ProjectStatusCaseInsensitiveConverter extends CaseInsensitiveEnumConverter<ProjectStatus> {

    protected ProjectStatusCaseInsensitiveConverter() {
        super(ProjectStatus.class);
    }
}
