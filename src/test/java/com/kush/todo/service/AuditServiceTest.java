package com.kush.todo.service;

import com.kush.todo.BaseTest;
import com.kush.todo.annotation.Auditable;
import com.kush.todo.mapper.AuditMapper;
import com.kush.todo.repository.AuditRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

class AuditServiceTest extends BaseTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private AuditMapper auditMapper;

    @InjectMocks
    private AuditService auditService;

    @Test
    void createDoNotBreakFlow() {
        Mockito.when(auditRepository.save(Mockito.any())).thenThrow(new RuntimeException());

        Assertions.assertDoesNotThrow(() -> auditService.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Mockito.mock(Auditable.class)));
    }
}