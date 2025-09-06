package com.kush.todo.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.kush.todo.dto.common.AuditActionResult;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table
public record Audit(
        @Id
        UUID id,
        UUID initiatorId,
        UUID targetId,
        AuditTargetType targetType,
        AuditActionType actionType,
        AuditActionResult actionResult,
        Instant createdAt,
        String spanId,
        String traceId,
        JsonNode details
) {
}
