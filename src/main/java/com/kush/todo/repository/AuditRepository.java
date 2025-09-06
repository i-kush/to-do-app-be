package com.kush.todo.repository;

import com.kush.todo.entity.Audit;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AuditRepository extends CrudRepository<Audit, UUID>, PagingAndSortingRepository<Audit, UUID> {

    Page<Audit> findAllByInitiatorId(UUID initiatorId, PageRequest of);
}
