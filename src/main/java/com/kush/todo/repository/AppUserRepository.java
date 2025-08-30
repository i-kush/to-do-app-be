package com.kush.todo.repository;

import com.kush.todo.dto.Permission;
import com.kush.todo.entity.AppUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Modifying
    @Query("""
           update app_user
           set is_locked = true,
               locked_at = now(),
               last_login_attempt_at = now(),
               login_attempts = coalesce(login_attempts, 0) + 1
           where id = :id
             and tenant_id = :tenantId
           """)
    void lockUser(UUID id, UUID tenantId);

    /**
     * @param loginAttempts Cannot be now() due to login attempts are dependent on the time window for the login attempts, that's why sometimes
     *                      we are setting this value to 1 since current attempt is outside the login attempt time window
     */
    @Modifying
    @Query("""
           update app_user
           set login_attempts = :loginAttempts,
               last_login_attempt_at = now()
           where id = :id
             and tenant_id = :tenantId
           """)
    void incrementLoginAttempts(UUID id, UUID tenantId, int loginAttempts);

    @Modifying
    @Query("""
           update app_user
           set login_attempts = null,
               last_login_attempt_at = null
           where id = :id
             and tenant_id = :tenantId
           """)
    void nullifyLoginAttempts(UUID id, UUID tenantId);

    @Modifying
    @Query("""
           update app_user
           set login_attempts = null,
               is_locked = false,
               locked_at = null,
               last_login_attempt_at = null
           where id in (:ids)
           """)
    void unlockUsers(Set<UUID> ids);

    @Query("""
           select id
           from app_user
           where is_locked = true
             and locked_at <= now() - interval '30 minutes'
           limit 500
           """)
    Set<UUID> findUserIdsToUnlock();

    @Modifying
    @Query("""
           update app_user
           set login_attempts = null,
               is_locked = false,
               locked_at = null,
               last_login_attempt_at = null
           where id = :id
           """)
    void unlockUser(UUID id);
}
