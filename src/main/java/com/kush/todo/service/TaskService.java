package com.kush.todo.service;

import com.kush.todo.dto.response.TaskDto;
import com.kush.todo.entity.Task;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.TaskMapper;
import com.kush.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskConverter;

    public TaskDto create(TaskDto taskDto) {
        Task task = taskConverter.toTask(taskDto);
        Task createdTask = taskRepository.save(task);
        return taskConverter.toTaskDto(createdTask);
    }

    public TaskDto findTaskById(long id) {
        Task task = taskRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("No such id " + id));

        return taskConverter.toTaskDto(task);
    }
}
