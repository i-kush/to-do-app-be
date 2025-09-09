package com.kush.todo.repository;

import com.kush.todo.constant.CommonErrorMessages;
import com.kush.todo.dto.TaskStatus;
import com.kush.todo.entity.Task;
import com.kush.todo.exception.NotFoundException;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;

public interface TaskRepository extends TenantAwareRepository<Task, UUID> {

    @Modifying
    @Query("delete from task t where t.id = :id and t.tenant_id = :tenantId")
    void deleteByIdAndTenantId(UUID id, UUID tenantId);

    @Modifying
    @Query("delete from task t where t.project_id = :projectId and t.tenant_id = :tenantId")
    void deleteByProjectIdAndTenantId(UUID projectId, UUID tenantId);

    @Modifying
    @Query("delete from task t where t.id = :id and t.project_id = :projectId and t.tenant_id = :tenantId")
    void deleteByIdAndProjectIdAndTenantId(UUID id, UUID projectId, UUID tenantId);

    @Modifying
    @Query("update task set status = :status where id = :id and project_id = :projectId and tenant_id = :tenantId")
    void setStatus(UUID id, UUID projectId, UUID tenantId, TaskStatus status);

    Page<Task> findAllByTenantIdAndProjectIdAndNameIgnoreCaseLike(Pageable pageable, UUID tenantId, UUID projectId, String exactKey);

    default Page<Task> findAllLike(Pageable pageable, UUID tenantId, UUID projectId, String exactKey) {
        return findAllByTenantIdAndProjectIdAndNameIgnoreCaseLike(pageable, tenantId, projectId, "%" + exactKey + "%");
    }

    Optional<Task> findByIdAndTenantIdAndProjectId(UUID id, UUID tenantId, UUID projectId);

    default Task findByIdAndTenantIdAndProjectIdRequired(UUID id, UUID tenantId, UUID projectId) {
        return findByIdAndTenantIdAndProjectId(id, tenantId, projectId)
                .orElseThrow(() -> new NotFoundException(String.format(CommonErrorMessages.PATTERN_NOT_FOUND, id)));
    }

    Page<Task> findAllByTenantIdAndProjectId(Pageable pageable, UUID tenantId, UUID projectId);

    boolean existsByIdAndProjectIdAndTenantId(UUID id, UUID projectId, UUID tenantId);
}
