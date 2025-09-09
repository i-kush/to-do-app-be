package com.kush.todo.controller;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.annotation.CommonApiErrors;
import com.kush.todo.constant.MetricsConstants;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import com.kush.todo.dto.request.ProjectRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ProjectResponseDto;
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
public class ProjectController {

    private final ProjectFacade projectFacade;

    @Operation(summary = "Create project", description = "Creates a project")
    @ApiResponses(value = @ApiResponse(responseCode = "201", description = "Successfully created project"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_CREATE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_CREATE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('PROJECT_WRITE')")
    @Auditable(actionType = AuditActionType.CREATE, targetType = AuditTargetType.PROJECT)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDto create(@Valid @RequestBody ProjectRequestDto request) {
        return projectFacade.create(request);
    }

    @Operation(summary = "Get project by ID", description = "Gets project details by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved project"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('PROJECT_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.PROJECT)
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProjectResponseDto get(@NotNull @PathVariable UUID id) {
        return projectFacade.findProjectById(id);
    }

    @Operation(summary = "Get projects", description = "Gets paginated projects list with details")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated projects"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('PROJECT_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.PROJECT)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomPage<ProjectResponseDto> getAll(@Min(1) @RequestParam int page, @Min(1) @Max(200) @RequestParam int size) {
        return projectFacade.findAll(page, size);
    }

    @Operation(summary = "Search projects", description = "Gets found paginated projects list with details")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved found paginated projects"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('PROJECT_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.PROJECT)
    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomPage<ProjectResponseDto> search(@Min(1) @RequestParam int page,
                                                 @Min(1) @Max(200) @RequestParam int size,
                                                 @NotEmpty @Size(min = 3, max = 30) String key) {
        return projectFacade.findAll(page, size, key);
    }

    @Operation(summary = "Update project by ID", description = "Updates project details by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully updated project"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('PROJECT_WRITE')")
    @Auditable(actionType = AuditActionType.UPDATE, targetType = AuditTargetType.PROJECT)
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProjectResponseDto update(@NotNull @PathVariable UUID id, @Valid @RequestBody ProjectRequestDto request) {
        return projectFacade.update(id, request);
    }

    @Operation(summary = "Delete project by ID", description = "Deletes project by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully deleted project"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_DELETE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_PROJECT, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_DELETE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('PROJECT_WRITE')")
    @Auditable(actionType = AuditActionType.DELETE, targetType = AuditTargetType.PROJECT)
    @DeleteMapping("{id}")
    public void delete(@NotNull @PathVariable UUID id) {
        projectFacade.delete(id);
    }
}