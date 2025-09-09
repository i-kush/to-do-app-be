package com.kush.todo.mapper;

import com.kush.todo.constant.MappingConstants;
import com.kush.todo.dto.request.TaskRequestDto;
import com.kush.todo.dto.response.TaskResponseDto;
import com.kush.todo.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public abstract class TaskMapper extends PageMapper {

    public abstract TaskResponseDto toTaskResponseDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "taskRequestDto.name")
    @Mapping(target = "description", source = "taskRequestDto.description")
    @Mapping(target = "assignedUserId", source = "taskRequestDto.assignedUserId")
    @Mapping(target = "status", constant = "OPEN")
    @Mapping(target = "createdAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Task toTask(UUID tenantId, UUID projectId, TaskRequestDto taskRequestDto);

    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "tenantId", source = "task.tenantId")
    @Mapping(target = "name", source = "taskRequestDto.name")
    @Mapping(target = "description", source = "taskRequestDto.description")
    @Mapping(target = "assignedUserId", source = "taskRequestDto.assignedUserId")
    @Mapping(target = "status", source = "taskRequestDto.status")
    @Mapping(target = "createdAt", source = "task.createdAt")
    @Mapping(target = "updatedAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    public abstract Task toTask(UUID projectId, Task task, TaskRequestDto taskRequestDto);
}
