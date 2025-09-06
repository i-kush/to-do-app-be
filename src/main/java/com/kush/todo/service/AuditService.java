package com.kush.todo.service;

import com.kush.todo.annotation.Auditable;
import com.kush.todo.config.AsyncConfig;
import com.kush.todo.dto.common.AuditActionResult;
import com.kush.todo.dto.response.AuditResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.mapper.AuditMapper;
import com.kush.todo.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;

    @Transactional
    @Async(AsyncConfig.THREAD_POOL_ASYNC)
    public void create(UUID initiatorId, UUID targetId, Auditable auditable) {
        create(initiatorId, targetId, auditable, AuditActionResult.SUCCESS, null);
    }

    @Transactional
    @Async(AsyncConfig.THREAD_POOL_ASYNC)
    public void create(UUID initiatorId, UUID targetId, Auditable auditable, Throwable e) {
        create(initiatorId, targetId, auditable, AuditActionResult.FAILURE, e);
    }

    private void create(UUID initiatorId, UUID targetId, Auditable auditable, AuditActionResult actionResult, Throwable e) {
        try {
            log.info("Create audit for targetId={}", targetId);
            auditRepository.save(auditMapper.toAudit(initiatorId, targetId, auditable, actionResult, e));
        } catch (RuntimeException ex) {
            log.error("Cannot create audit record", ex);
        }
    }

    @Transactional(readOnly = true)
    public CustomPage<AuditResponseDto> findAllByUserId(UUID userId, int page, int size) {
        Page<AuditResponseDto> pages = auditRepository.findAllByInitiatorId(userId, PageRequest.of(page - 1, size))
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