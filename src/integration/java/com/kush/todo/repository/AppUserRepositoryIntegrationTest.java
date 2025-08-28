package com.kush.todo.repository;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.Permission;
import com.kush.todo.entity.AppUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

class AppUserRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void findUserPermissions() {
        Optional<AppUser> optionalAppUser = appUserRepository.findByUsername(IntegrationTestDataBuilder.TEST_USERNAME);

        Assertions.assertTrue(optionalAppUser.isPresent());

        AppUser appUser = optionalAppUser.get();
        List<Permission> userPermissions = appUserRepository.findUserPermissions(appUser.id(), appUser.tenantId());

        Assertions.assertFalse(CollectionUtils.isEmpty(userPermissions));
    }
}