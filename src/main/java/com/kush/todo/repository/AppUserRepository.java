package com.kush.todo.repository;

import com.kush.todo.dto.Permission;
import com.kush.todo.entity.AppUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;

public interface AppUserRepository extends TenantAwareRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);

    @Modifying
    @Query("delete from app_user au where au.id = :id and au.tenant_id = :tenantId")
    void deleteByIdAndTenantId(UUID id, UUID tenantId);

    @Query("""
           select p.name
           from app_user u
                    left join role_permission rp on
               u.role_id = rp.role_id
                    left join permission p on
               rp.permission_id = p.name
           where u.id = :id
             and u.tenant_id = :tenantId;
           """)
    List<Permission> findUserPermissions(UUID id, UUID tenantId);
}
