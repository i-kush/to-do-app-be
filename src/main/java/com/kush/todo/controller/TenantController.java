package com.kush.todo.controller;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.annotation.CommonApiErrors;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.request.UpdateTenantRequestDto;
import com.kush.todo.dto.response.AsyncOperationQueuedResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.TenantDeleteResponseDto;
import com.kush.todo.dto.response.TenantDetailsResponseDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.facade.TenantFacade;
import com.kush.todo.service.TenantService;
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
@RequestMapping("api/tenants")
@RequiredArgsConstructor
@Tag(name = "tenants")
public class TenantController {

    private final TenantService tenantService;
    private final TenantFacade tenantFacade;

    @Operation(summary = "Create tenant", description = "Creates a tenant with the specific settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created tenant"),
    })
    @CommonApiErrors
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('TENANT_WRITE')")
    @Auditable(actionType = AuditActionType.CREATE, targetType = AuditTargetType.TENANT)
    public TenantDetailsResponseDto create(@Valid @RequestBody CreateTenantRequestDto tenantDto) {
        return tenantFacade.create(tenantDto);
    }

    @Operation(summary = "Get tenant by ID", description = "Gets tenant details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tenant"),
    })
    @CommonApiErrors
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.TENANT)
    public TenantResponseDto get(@NotNull @PathVariable UUID id) {
        return tenantService.findByIdRequired(id);
    }

    @Operation(summary = "Create tenant async", description = "Creates a tenant with the specific settings in the async mode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Async tenant creation launched successfully"),
    })
    @CommonApiErrors
    @PostMapping(value = "async", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_WRITE')")
    @Auditable(actionType = AuditActionType.CREATE, targetType = AuditTargetType.TENANT_ASYNC)
    public AsyncOperationQueuedResponseDto createAsync(@Valid @RequestBody CreateTenantRequestDto tenantDto) {
        return tenantFacade.createAsync(tenantDto);
    }

    @Operation(summary = "Get tenants", description = "Gets paginated tenants list with details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated tenants"),
    })
    @CommonApiErrors
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_READ')")
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.TENANT)
    public CustomPage<TenantResponseDto> getAll(@Min(1) @RequestParam int page, @Min(1) @Max(200) @RequestParam int size) {
        return tenantService.findAll(page, size);
    }

    @Operation(summary = "Update tenant by ID", description = "Updates tenant details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated tenant"),
    })
    @CommonApiErrors
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_WRITE')")
    @Auditable(actionType = AuditActionType.UPDATE, targetType = AuditTargetType.TENANT)
    public TenantResponseDto update(@NotNull @PathVariable UUID id, @Valid @RequestBody UpdateTenantRequestDto tenantDto) {
        return tenantService.update(id, tenantDto);
    }

    @Operation(summary = "Delete tenant by ID", description = "Deletes tenant by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted tenant"),
    })
    @CommonApiErrors
    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('TENANT_WRITE')")
    @Auditable(actionType = AuditActionType.DELETE, targetType = AuditTargetType.TENANT)
    public TenantDeleteResponseDto delete(@NotNull @PathVariable UUID id) {
        return tenantFacade.delete(id);
    }

    @Operation(summary = "Delete tenant async", description = "Deletes tenant by ID in the async mode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Async tenant deletion launched successfully"),
    })
    @CommonApiErrors
    @DeleteMapping("{id}/async")
    @PreAuthorize("hasAuthority('TENANT_WRITE')")
    @Auditable(actionType = AuditActionType.DELETE, targetType = AuditTargetType.TENANT_ASYNC)
    public AsyncOperationQueuedResponseDto deleteAsync(@NotNull @PathVariable UUID id) {
        return tenantFacade.deleteAsync(id);
    }
}
