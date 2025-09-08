package com.kush.todo.controller;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.annotation.CommonApiErrors;
import com.kush.todo.constant.MetricsConstants;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.service.AppUserService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "users")
public class AppUserController {

    private final AppUserService appUserService;
    private final CurrentUser currentUser;

    @Operation(summary = "Create user", description = "Creates a user")
    @ApiResponses(value = @ApiResponse(responseCode = "201", description = "Successfully created user"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_CREATE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_CREATE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Auditable(actionType = AuditActionType.CREATE, targetType = AuditTargetType.USER)
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AppUserResponseDto create(@Valid @RequestBody AppUserRequestDto request) {
        return appUserService.create(request);
    }

    @Operation(summary = "Get logged in user", description = "Gets logged in user details")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved user"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.USER)
    @GetMapping(value = "me", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppUserResponseDto me() {
        return appUserService.findByIdRequired(currentUser.getId());
    }

    @Operation(summary = "Get user by ID", description = "Gets user details by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved user"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('USER_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.USER)
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppUserResponseDto get(@NotNull @PathVariable UUID id) {
        return appUserService.findByIdRequired(id);
    }

    @Operation(summary = "Get users", description = "Gets paginated users list with details")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated users"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('USER_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.USER)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomPage<AppUserResponseDto> getAll(@Min(1) @RequestParam int page, @Min(1) @Max(200) @RequestParam int size) {
        return appUserService.findAll(page, size);
    }

    @Operation(summary = "Update user by ID", description = "Updates user details by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully updated user"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Auditable(actionType = AuditActionType.UPDATE, targetType = AuditTargetType.USER)
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppUserResponseDto update(@NotNull @PathVariable UUID id, @Valid @RequestBody AppUserRequestDto request) {
        return appUserService.update(id, request);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes user by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully deleted user"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_DELETE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_DELETE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Auditable(actionType = AuditActionType.DELETE, targetType = AuditTargetType.USER)
    @DeleteMapping("{id}")
    public void delete(@NotNull @PathVariable UUID id) {
        appUserService.delete(id);
    }

    @Operation(summary = "Unlock user", description = "Unlocks a user")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Unlocks user by ID"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_USER, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_UPDATE},
             recordFailuresOnly = true)
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Auditable(actionType = AuditActionType.UPDATE, targetType = AuditTargetType.USER)
    @PostMapping("{id}/unlock")
    public void unlock(@NotNull @PathVariable UUID id) {
        appUserService.unlockUser(id);
    }
}
