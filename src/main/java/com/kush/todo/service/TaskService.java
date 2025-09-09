package com.kush.todo.service;

import com.kush.todo.constant.CommonErrorMessages;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.TaskRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.TaskResponseDto;
import com.kush.todo.entity.Task;
import com.kush.todo.exception.NotFoundException;
import com.kush.todo.mapper.TaskMapper;
import com.kush.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final CurrentUser currentUser;

    @Transactional
    public TaskResponseDto create(UUID projectId, TaskRequestDto request) {
        Task task = taskMapper.toTask(currentUser.getTenantId(), projectId, request);
        return taskMapper.toTaskResponseDto(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponseDto findByIdRequired(UUID projectId, UUID taskId) {
        return taskMapper.toTaskResponseDto(taskRepository.findByIdAndTenantIdAndProjectIdRequired(taskId, currentUser.getTenantId(), projectId));
    }

    @Transactional
    public TaskResponseDto update(UUID projectId, UUID taskId, TaskRequestDto request) {
        Task task = taskMapper.toTask(projectId, taskRepository.findByIdAndTenantIdAndProjectIdRequired(taskId, currentUser.getTenantId(), projectId), request);
        return taskMapper.toTaskResponseDto(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public CustomPage<TaskResponseDto> findAll(UUID projectId, int page, int size) {
        Page<TaskResponseDto> pages = taskRepository
                .findAllByTenantIdAndProjectId(PageRequest.of(page - 1, size), currentUser.getTenantId(), projectId)
                .map(taskMapper::toTaskResponseDto);
        return taskMapper.toCustomPage(pages);
    }

    @Transactional(readOnly = true)
    public CustomPage<TaskResponseDto> findAll(UUID projectId, int page, int size, String key) {
        Page<TaskResponseDto> pages = taskRepository
                .findAllLike(PageRequest.of(page - 1, size), currentUser.getTenantId(), projectId, key)
                .map(taskMapper::toTaskResponseDto);
        return taskMapper.toCustomPage(pages);
    }

    @Transactional
    public void deleteByProjectId(UUID projectId) {
        taskRepository.deleteByProjectIdAndTenantId(projectId, currentUser.getTenantId());
    }

    @Transactional
    public void delete(UUID projectId, UUID taskId) {
        verifyExists(taskId, projectId);
        taskRepository.deleteByIdAndProjectIdAndTenantId(taskId, projectId, currentUser.getTenantId());
    }

    @Transactional(readOnly = true)
    public void verifyExists(UUID taskId, UUID projectId) {
        if (!taskRepository.existsByIdAndProjectIdAndTenantId(taskId, projectId, currentUser.getTenantId())) {
            throw new NotFoundException(String.format(CommonErrorMessages.PATTERN_NOT_FOUND, taskId));
        }
    }
}