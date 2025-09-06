package com.kush.todo.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.kush.todo.dto.common.AuditActionResult;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AuditRequestDto(
        UUID targetId,
        AuditTargetType targetType,
        AuditActionType actionType,
        AuditActionResult actionResult,
        JsonNode details
) {
}
