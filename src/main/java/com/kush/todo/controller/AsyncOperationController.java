package com.kush.todo.controller;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.annotation.CommonApiErrors;
import com.kush.todo.constant.MetricsConstants;
import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.service.AsyncOperationService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/async/operations")
@RequiredArgsConstructor
@Tag(name = "operations")
public class AsyncOperationController {

    private final AsyncOperationService asyncOperationService;
    private final CurrentUser currentUser;

    @Operation(summary = "Get async result by operation ID", description = "Gets async operation result by ID")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Successfully retrieved async operation"))
    @CommonApiErrors
    @Timed(value = MetricsConstants.TIMER_ENDPOINT,
           extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_OPERATION, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ})
    @Counted(value = MetricsConstants.COUNT_ENDPOINT_ERROR,
             extraTags = {MetricsConstants.TAG_DOMAIN_NAME, MetricsConstants.TAG_DOMAIN_OPERATION, MetricsConstants.TAG_OPERATION_NAME, MetricsConstants.TAG_OPERATION_READ},
             recordFailuresOnly = true)
    @Auditable(actionType = AuditActionType.READ, targetType = AuditTargetType.OPERATION)
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public <T> AsyncOperationDto<T> getOperation(@NotNull @PathVariable UUID id) {
        return asyncOperationService.getOperation(id, currentUser.getTenantId());
    }
}
