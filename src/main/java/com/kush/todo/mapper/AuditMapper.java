package com.kush.todo.mapper;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.dto.common.AuditActionResult;
import com.kush.todo.dto.response.AuditResponseDto;
import com.kush.todo.entity.Audit;
import org.mapstruct.Mapper;
import org.slf4j.MDC;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class AuditMapper extends PageMapper {

    @Autowired
    private ObjectMapper objectMapper;

    public Audit toAudit(UUID tenantId,
                         UUID initiatorId,
                         UUID targetId,
                         Auditable auditable,
                         AuditActionResult actionResult,
                         Exception e) {
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

    private JsonNode toAuditDetails(Exception e) {
        if (e == null) {
            return null;
        }
        ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("message", e.getMessage());
        objectNode.put("type", e.getClass().getName());

        return objectNode;
    }

    public abstract AuditResponseDto toAuditDto(Audit audit);
}
