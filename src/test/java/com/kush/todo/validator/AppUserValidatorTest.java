package com.kush.todo.validator;

import com.kush.todo.BaseTest;
import com.kush.todo.dto.CurrentUser;
import com.kush.todo.dto.Role;
import com.kush.todo.dto.request.AppUserRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class AppUserValidatorTest extends BaseTest {

    private final AppUserValidator appUserValidator = new AppUserValidator();

    @Test
    void validateTargetRole() {
        AppUserRequestDto appUserRequestDto = AppUserRequestDto.builder()
                                                               .roleId(Role.GLOBAL_ADMIN)
                                                               .build();
        CurrentUser currentUser = CurrentUser.builder()
                                             .role(Role.GLOBAL_ADMIN)
                                             .build();

        Assertions.assertDoesNotThrow(() -> appUserValidator.validateTargetRole(appUserRequestDto, currentUser));
    }

    @Test
    void validateTargetRoleFailure() {
        AppUserRequestDto appUserRequestDto = AppUserRequestDto.builder()
                                                               .roleId(Role.GLOBAL_ADMIN)
                                                               .build();
        CurrentUser currentUser = CurrentUser.builder()
                                             .role(Role.TENANT_ADMIN)
                                             .build();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                                     () -> appUserValidator.validateTargetRole(appUserRequestDto, currentUser));
        Assertions.assertEquals("Invalid target role", exception.getMessage());
    }

    @Test
    void validateDelete() {
        UUID id = UUID.randomUUID();
        CurrentUser currentUser = CurrentUser.builder()
                                             .id(id)
                                             .build();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                                                                     () -> appUserValidator.validateDelete(id, currentUser));
        Assertions.assertEquals("Cannot delete yourself", exception.getMessage());
    }
}