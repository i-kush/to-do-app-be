package com.kush.todo.mapper;

import com.kush.todo.constant.MappingConstants;
import com.kush.todo.dto.request.ProjectRequestDto;
import com.kush.todo.dto.response.ProjectResponseDto;
import com.kush.todo.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public abstract class ProjectMapper extends PageMapper {

    public abstract ProjectResponseDto toProjectResponseDto(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "projectRequestDto.name")
    @Mapping(target = "description", source = "projectRequestDto.description")
    @Mapping(target = "status", constant = "OPEN")
    @Mapping(target = "createdAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Project toProject(UUID tenantId, ProjectRequestDto projectRequestDto);

    @Mapping(target = "id", source = "project.id")
    @Mapping(target = "tenantId", source = "project.tenantId")
    @Mapping(target = "name", source = "projectRequestDto.name")
    @Mapping(target = "description", source = "projectRequestDto.description")
    @Mapping(target = "status", source = "projectRequestDto.status")
    @Mapping(target = "createdAt", source = "project.createdAt")
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Project toProject(Project project, ProjectRequestDto projectRequestDto);
}
