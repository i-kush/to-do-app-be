package com.kush.todo.controller;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.annotation.CommonApiErrors;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.service.AppUserService;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created user"),
    })
    @CommonApiErrors
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Auditable(actionType = AuditActionType.CREATE, targetType = AuditTargetType.USER)
    public AppUserResponseDto create(@Valid @RequestBody AppUserRequestDto userDto) {
        return appUserService.create(userDto);
    }

    @Operation(summary = "Get logged in user", description = "Gets logged in user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
    })
    @CommonApiErrors
    @GetMapping(value = "me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.USER)
    public AppUserResponseDto me() {
        return appUserService.findByIdRequired(currentUser.getId());
    }

    @Operation(summary = "Get user by ID", description = "Gets user details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
    })
    @CommonApiErrors
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('USER_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.USER)
    public AppUserResponseDto get(@NotNull @PathVariable UUID id) {
        return appUserService.findByIdRequired(id);
    }

    @Operation(summary = "Get users", description = "Gets paginated users list with details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated users"),
    })
    @CommonApiErrors
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('USER_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.USER)
    public CustomPage<AppUserResponseDto> getAll(@Min(1) @RequestParam int page, @Min(1) @Max(200) @RequestParam int size) {
        return appUserService.findAll(page, size);
    }

    @Operation(summary = "Update user by ID", description = "Updates user details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
    })
    @CommonApiErrors
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Auditable(actionType = AuditActionType.UPDATE, targetType = AuditTargetType.USER)
    public AppUserResponseDto update(@NotNull @PathVariable UUID id, @Valid @RequestBody AppUserRequestDto userDto) {
        return appUserService.update(id, userDto);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
    })
    @CommonApiErrors
    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Auditable(actionType = AuditActionType.DELETE, targetType = AuditTargetType.USER)
    public void delete(@NotNull @PathVariable UUID id) {
        appUserService.delete(id);
    }

    @Operation(summary = "Unlock user", description = "Unlocks a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unlocks user by ID"),
    })
    @CommonApiErrors
    @PostMapping("{id}/unlock")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    @Auditable(actionType = AuditActionType.UPDATE, targetType = AuditTargetType.USER)
    public void unlock(@NotNull @PathVariable UUID id) {
        appUserService.unlockUser(id);
    }
}
