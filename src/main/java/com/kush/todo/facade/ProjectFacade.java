package com.kush.todo.facade;

import com.kush.todo.dto.ProjectStatus;
import com.kush.todo.dto.TaskStatus;
import com.kush.todo.dto.request.ProjectRequestDto;
import com.kush.todo.dto.request.TaskRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ProjectResponseDto;
import com.kush.todo.dto.response.TaskResponseDto;
import com.kush.todo.service.AppUserService;
import com.kush.todo.service.ProjectService;
import com.kush.todo.service.TaskService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectFacade {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final AppUserService appUserService;

    @Transactional
    public ProjectResponseDto create(ProjectRequestDto request) {
        return projectService.create(request);
    }

    @Transactional(readOnly = true)
    public ProjectResponseDto findProjectById(UUID id) {
        return projectService.findByIdRequired(id);
    }

    @Transactional(readOnly = true)
    public CustomPage<ProjectResponseDto> findAll(int page, int size) {
        return projectService.findAll(page, size);
    }

    @Transactional(readOnly = true)
    public CustomPage<ProjectResponseDto> findAll(int page, int size, String key) {
        return projectService.findAll(page, size, key);
    }

    @Transactional
    public ProjectResponseDto update(UUID id, ProjectRequestDto request) {
        return projectService.update(id, request);
    }

    @Transactional
    public void setStatus(UUID id, ProjectStatus status) {
        projectService.setStatus(id, status);
    }

    @Transactional
    public void delete(UUID id) {
        taskService.deleteByProjectId(id);
        projectService.delete(id);
    }

    @Transactional
    public TaskResponseDto createTask(UUID projectId, TaskRequestDto request) {
        verifyTaskManagement(projectId, request);
        return taskService.create(projectId, request);
    }

    @Transactional(readOnly = true)
    public TaskResponseDto findTaskById(UUID projectId, UUID taskId) {
        return taskService.findByIdRequired(projectId, taskId);
    }

    @Transactional(readOnly = true)
    public CustomPage<TaskResponseDto> findAllTasks(UUID projectId, int page, int size) {
        return taskService.findAll(projectId, page, size);
    }

    @Transactional(readOnly = true)
    public CustomPage<TaskResponseDto> findAllTasks(UUID projectId, int page, int size, String key) {
        return taskService.findAll(projectId, page, size, key);
    }

    @Transactional
    public TaskResponseDto updateTask(UUID projectId, UUID taskId, TaskRequestDto request) {
        verifyTaskManagement(projectId, request);
        return taskService.update(projectId, taskId, request);
    }

    @Transactional
    public void setTaskStatus(UUID projectId, UUID taskId, TaskStatus status) {
        projectService.verifyExists(projectId);
        taskService.setStatus(projectId, taskId, status);
    }

    @Transactional
    public void deleteTask(UUID projectId, UUID taskId) {
        taskService.delete(projectId, taskId);
    }

    private void verifyTaskManagement(UUID projectId, TaskRequestDto request) {
        projectService.verifyExists(projectId);
        if (request.assignedUserId() != null) {
            appUserService.verifyExists(request.assignedUserId());
        }
    }
}
