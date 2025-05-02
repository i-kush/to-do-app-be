package com.kush.todo.repository;

import com.kush.todo.entity.Tenant;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TenantRepository extends JpaRepository<Tenant, UUID>, PagingAndSortingRepository<Tenant, UUID> {

    boolean existsByName(String name);
}
