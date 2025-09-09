package com.kush.todo.controller;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.annotation.CommonApiErrors;
import com.kush.todo.constant.MetricsConstants;
import com.kush.todo.dto.TaskStatus;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import com.kush.todo.dto.request.TaskRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.TaskResponseDto;
import com.kush.todo.facade.ProjectFacade;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/projects")
@RequiredArgsConstructor
public class TaskController {

    private final ProjectFacade projectFacade;

    @Operation(summary = "Create task", description = "Creates a task")
    @ApiResponses(value = @ApiResponse(responseCode = "201", description = "Successfully created task"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_CREATE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_CREATE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('TASK_WRITE')")
    @Auditable(actionType = AuditActionType.CREATE, targetType = AuditTargetType.TASK)
    @PostMapping(value = "{projectId}/tasks", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDto createTask(@NotNull @PathVariable UUID projectId, @Valid @RequestBody TaskRequestDto request) {
        return projectFacade.createTask(projectId, request);
    }

    @Operation(summary = "Get task by ID", description = "Gets task details by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved task"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('TASK_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.TASK)
    @GetMapping(value = "{projectId}/tasks/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskResponseDto getTask(@NotNull @PathVariable UUID projectId, @NotNull @PathVariable UUID taskId) {
        return projectFacade.findTaskById(projectId, taskId);
    }

    @Operation(summary = "Get tasks", description = "Gets paginated tasks list with details")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated tasks"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('TASK_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.TASK)
    @GetMapping(value = "{projectId}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomPage<TaskResponseDto> getAllTasks(@NotNull @PathVariable UUID projectId,
                                                   @Min(1) @RequestParam int page,
                                                   @Min(1) @Max(200) @RequestParam int size) {
        return projectFacade.findAllTasks(projectId, page, size);
    }

    @Operation(summary = "Search tasks", description = "Gets found paginated tasks list with details")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved found paginated tasks"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('TASK_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.TASK)
    @GetMapping(value = "{projectId}/tasks/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomPage<TaskResponseDto> searchTasks(@NotNull @PathVariable UUID projectId,
                                                   @Min(1) @RequestParam int page,
                                                   @Min(1) @Max(200) @RequestParam int size,
                                                   @NotEmpty @Size(min = 3, max = 30) String key) {
        return projectFacade.findAllTasks(projectId, page, size, key);
    }

    @Operation(summary = "Update task by ID", description = "Updates task details by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully updated task"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('TASK_WRITE')")
    @Auditable(actionType = AuditActionType.UPDATE, targetType = AuditTargetType.TASK)
    @PutMapping(value = "{projectId}/tasks/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TaskResponseDto updateTask(@NotNull @PathVariable UUID projectId,
                                      @NotNull @PathVariable UUID taskId,
                                      @Valid @RequestBody TaskRequestDto request) {
        return projectFacade.updateTask(projectId, taskId, request);
    }

    @Operation(summary = "Set task status by ID", description = "Sets task status by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully updated task status"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('TASK_WRITE')")
    @Auditable(actionType = AuditActionType.UPDATE, targetType = AuditTargetType.TASK)
    @PutMapping(value = "{projectId}/tasks/{taskId}/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateTaskStatus(@NotNull @PathVariable UUID projectId,
                                 @NotNull @PathVariable UUID taskId,
                                 @NotNull @PathVariable TaskStatus status) {
        projectFacade.setTaskStatus(projectId, taskId, status);
    }

    @Operation(summary = "Delete task by ID", description = "Deletes task by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully deleted task"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_DELETE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_TASK, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_DELETE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('TASK_WRITE')")
    @Auditable(actionType = AuditActionType.DELETE, targetType = AuditTargetType.TASK)
    @DeleteMapping("{projectId}/tasks/{taskId}")
    public void deleteTask(@NotNull @PathVariable UUID projectId, @NotNull @PathVariable UUID taskId) {
        projectFacade.deleteTask(projectId, taskId);
    }
}