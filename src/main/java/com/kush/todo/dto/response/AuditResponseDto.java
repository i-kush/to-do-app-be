package com.kush.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.kush.todo.dto.common.AuditActionResult;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AuditResponseDto(
        UUID id,
        UUID tenantId,
        UUID initiatorId,
        UUID targetId,
        AuditTargetType targetType,
        AuditActionType actionType,
        AuditActionResult actionResult,
        Instant createdAt,
        String spanId,
        String traceId,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        JsonNode details
) {
}
