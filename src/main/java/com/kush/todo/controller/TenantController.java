package com.kush.todo.controller;

import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.Vector;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "Create tenant", description = "Creates a tenant with the specific settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created tenant"),
            @ApiResponse(responseCode = "4**/5**", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponseDto create(@Valid @RequestBody TenantRequestDto tenantDto) {
        new Vector<>();
        return tenantService.create(tenantDto);
    }

    @Operation(summary = "Get tenant by ID", description = "Gets tenant details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tenant"),
            @ApiResponse(responseCode = "4**/5**", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TenantResponseDto get(@NotNull @PathVariable UUID id) {
        return tenantService.findById(id);
    }

    @Operation(summary = "Get tenants", description = "Gets paginated tenants list with details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated tenants"),
            @ApiResponse(responseCode = "4**/5**", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomPage<TenantResponseDto> getAll(@Min(1) @RequestParam int page, @Min(1) @Max(200) @RequestParam int size) {
        return tenantService.findAll(page, size);
    }

    @Operation(summary = "Update tenant by ID", description = "Updates tenant details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated tenant"),
            @ApiResponse(responseCode = "4**/5**", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TenantResponseDto update(@NotNull @PathVariable UUID id, @Valid @RequestBody TenantRequestDto tenantDto) {
        return tenantService.update(id, tenantDto);
    }

    @Operation(summary = "Delete tenant by ID", description = "Deletes tenant by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted tenant"),
            @ApiResponse(responseCode = "4**/5**", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @DeleteMapping("{id}")
    public void delete(@NotNull @PathVariable UUID id) {
        tenantService.delete(id);
    }
}
