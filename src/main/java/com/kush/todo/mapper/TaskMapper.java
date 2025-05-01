package com.kush.todo.mapper;

import com.kush.todo.dto.response.TaskDto;
import com.kush.todo.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class TaskMapper {

    @Mapping(target = "id", ignore = true)
    public abstract Task toTask(TaskDto taskDto);

    public abstract TaskDto toTaskDto(Task task);
}
