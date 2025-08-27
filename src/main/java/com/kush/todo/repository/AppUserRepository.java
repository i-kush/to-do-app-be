package com.kush.todo.repository;

import com.kush.todo.entity.AppUser;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;

public interface AppUserRepository extends TenantAwareRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);

    @Modifying
    @Query("delete from app_user au where au.id = :id and au.tenant_id = :tenantId")
    void deleteByIdAndTenantId(UUID id, UUID tenantId);
}
