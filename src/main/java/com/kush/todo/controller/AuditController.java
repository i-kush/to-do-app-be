package com.kush.todo.controller;

import com.kush.todo.annotation.CommonApiErrors;
import com.kush.todo.dto.response.AuditResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/audit")
@RequiredArgsConstructor
@Tag(name = "audit")
public class AuditController {

    private final AuditService auditService;

    @Operation(summary = "Find my audit entries", description = "Gets paginated audit entries for the logged in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated audit entries for the logged in user"),
    })
    @CommonApiErrors
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_READ')")
    public CustomPage<AuditResponseDto> findAllMine(@Min(1) @RequestParam int page, @Min(1) @Max(200) @RequestParam int size) {
        return auditService.findAllMine(page, size);
    }

    @Operation(summary = "Find all audit entries", description = "Gets paginated audit entries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated audit entries"),
    })
    @CommonApiErrors
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('TENANT_READ')")
    public CustomPage<AuditResponseDto> findAll(@Min(1) @RequestParam int page, @Min(1) @Max(200) @RequestParam int size) {
        return auditService.findAll(page, size);
    }
}