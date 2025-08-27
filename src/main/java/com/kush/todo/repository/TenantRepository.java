package com.kush.todo.repository;

import com.kush.todo.entity.Tenant;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TenantRepository extends CrudRepository<Tenant, UUID>, PagingAndSortingRepository<Tenant, UUID> {
}
