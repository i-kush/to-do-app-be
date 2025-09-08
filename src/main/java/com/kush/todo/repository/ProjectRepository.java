package com.kush.todo.repository;

import com.kush.todo.entity.Project;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;

public interface ProjectRepository extends TenantAwareRepository<Project, UUID> {

    @Modifying
    @Query("delete from project p where p.id = :id and p.tenant_id = :tenantId")
    void deleteByIdAndTenantId(UUID id, UUID tenantId);

    Page<Project> findAllByTenantIdAndNameIgnoreCaseLike(Pageable pageable, UUID tenantId, String exactKey);

    default Page<Project> findAllLike(Pageable pageable, UUID tenantId, String exactKey) {
        return findAllByTenantIdAndNameIgnoreCaseLike(pageable, tenantId, "%" + exactKey + "%");
    }

}
