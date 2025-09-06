package com.kush.todo.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kush.todo.annotation.Auditable;
import com.kush.todo.dto.common.AuditActionResult;
import com.kush.todo.dto.response.AuditResponseDto;
import com.kush.todo.entity.Audit;
import org.mapstruct.Mapper;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.UUID;

@Mapper
public abstract class AuditMapper extends PageMapper {

    public Audit toAudit(UUID tenantId,
                         UUID initiatorId,
                         UUID targetId,
                         Auditable auditable,
                         AuditActionResult actionResult,
                         Throwable e) {
        return Audit.builder()
                    .initiatorId(initiatorId)
                    .tenantId(tenantId)
                    .targetId(targetId)
                    .targetType(auditable.targetType())
                    .actionType(auditable.actionType())
                    .actionResult(actionResult)
                    .createdAt(Instant.now())
                    .spanId(MDC.get("spanId"))
                    .traceId(MDC.get("traceId"))
                    .details(toAuditDetails(e))
                    .build();
    }

    private JsonNode toAuditDetails(Throwable e) {
        if (e == null) {
            return null;
        }
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        objectNode.put("message", e.getMessage());
        objectNode.put("type", e.getClass().getName());

        return objectNode;
    }

    public abstract AuditResponseDto toAuditDto(Audit audit);
}
