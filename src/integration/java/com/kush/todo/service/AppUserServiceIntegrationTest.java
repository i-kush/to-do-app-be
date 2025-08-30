package com.kush.todo.service;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.entity.AppUser;
import com.kush.todo.repository.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

class AppUserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void unlockUsers() {
        AppUser lockedUserToUnlock1 = IntegrationTestDataBuilder.buildLockedAppUser(Instant.now().minus(50, ChronoUnit.MINUTES),
                                                                                    defaultTenantId);
        AppUser createdLockedUserToUnlock1 = appUserRepository.save(lockedUserToUnlock1);
        AppUser lockedUserToUnlock2 = IntegrationTestDataBuilder.buildLockedAppUser(Instant.now().minus(50, ChronoUnit.MINUTES),
                                                                                    defaultTenantId);
        AppUser createdLockedUserToUnlock2 = appUserRepository.save(lockedUserToUnlock2);
        AppUser lockUserToNotUnlock = IntegrationTestDataBuilder.buildLockedAppUser(Instant.now(), defaultTenantId);
        AppUser createdLockUserToNotUnlock = appUserRepository.save(lockUserToNotUnlock);

        Assertions.assertDoesNotThrow(appUserService::unlockUsers);

        assertLockUsersUnlocked(createdLockedUserToUnlock1);
        assertLockUsersUnlocked(createdLockedUserToUnlock2);

        Optional<AppUser> optionalUser = appUserRepository.findByIdAndTenantId(createdLockUserToNotUnlock.id(), defaultTenantId);
        Assertions.assertTrue(optionalUser.isPresent());
        AppUser user = optionalUser.get();
        Assertions.assertTrue(user.isLocked());
        Assertions.assertNotNull(user.lockedAt());
        Assertions.assertNotNull(user.loginAttempts());
        Assertions.assertNotNull(user.lastLoginAttemptAt());
    }

    private void assertLockUsersUnlocked(AppUser lockedUserToUnlock) {
        Optional<AppUser> optionalUser = appUserRepository.findByIdAndTenantId(lockedUserToUnlock.id(), defaultTenantId);
        Assertions.assertTrue(optionalUser.isPresent());
        AppUser user = optionalUser.get();
        Assertions.assertFalse(user.isLocked());
        Assertions.assertNull(user.lockedAt());
        Assertions.assertNull(user.loginAttempts());
        Assertions.assertNull(user.lastLoginAttemptAt());
    }
}
