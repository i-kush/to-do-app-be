package com.kush.todo.repository;

import com.kush.todo.entity.AppUser;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AppUserRepository extends CrudRepository<AppUser, UUID>, PagingAndSortingRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);
}
