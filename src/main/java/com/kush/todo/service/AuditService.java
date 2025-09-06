package com.kush.todo.service;

import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.request.AuditRequestDto;
import com.kush.todo.dto.response.AuditResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.entity.Audit;
import com.kush.todo.mapper.AuditMapper;
import com.kush.todo.repository.AuditRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;
    private final CurrentUser currentUser;

    @Transactional
    public AuditResponseDto create(AuditRequestDto auditRequestDto) {
        Audit audit = auditMapper.toAudit(currentUser.getId(), auditRequestDto);
        Audit createdAudit = auditRepository.save(audit);

        return auditMapper.toAuditDto(createdAudit);
    }

    @Transactional(readOnly = true)
    public CustomPage<AuditResponseDto> findAllMine(int page, int size) {
        Page<AuditResponseDto> pages = auditRepository.findAllByInitiatorId(currentUser.getId(), PageRequest.of(page - 1, size))
                                                      .map(auditMapper::toAuditDto);
        return auditMapper.toCustomPage(pages);
    }

    @Transactional(readOnly = true)
    public CustomPage<AuditResponseDto> findAll(int page, int size) {
        Page<AuditResponseDto> pages = auditRepository.findAll(PageRequest.of(page - 1, size))
                                                      .map(auditMapper::toAuditDto);
        return auditMapper.toCustomPage(pages);
    }
}