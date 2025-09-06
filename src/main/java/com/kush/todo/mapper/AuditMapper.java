package com.kush.todo.mapper;

import com.kush.todo.dto.request.AuditRequestDto;
import com.kush.todo.dto.response.AuditResponseDto;
import com.kush.todo.entity.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public abstract class AuditMapper extends PageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "targetId", source = "auditRequestDto.targetId")
    @Mapping(target = "targetType", source = "auditRequestDto.targetType")
    @Mapping(target = "actionType", source = "auditRequestDto.actionType")
    @Mapping(target = "actionResult", source = "auditRequestDto.actionResult")
    @Mapping(target = "createdAt", expression = MappingConstants.EXPRESSION_TIMESTAMP_NOW)
    @Mapping(target = "spanId", expression = MappingConstants.EXPRESSION_MDC_SPAN_ID)
    @Mapping(target = "traceId", expression = MappingConstants.EXPRESSION_MDC_TRACE_ID)
    @Mapping(target = "details", source = "auditRequestDto.details")
    public abstract Audit toAudit(UUID initiatorId, AuditRequestDto auditRequestDto);

    public abstract AuditResponseDto toAuditDto(Audit audit);
}
