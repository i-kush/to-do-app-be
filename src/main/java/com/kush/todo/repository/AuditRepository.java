package com.kush.todo.repository;

import com.kush.todo.entity.Audit;

import java.util.UUID;

public interface AuditRepository extends TenantAwareRepository<Audit, UUID> {
}
