package com.kush.todo.validator;

import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.common.Role;
import com.kush.todo.dto.request.AppUserRequestDto;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class AppUserValidator {

    public void validateTargetRole(AppUserRequestDto appUserRequestDto, CurrentUser currentUser) {
        if (appUserRequestDto.roleId() == Role.GLOBAL_ADMIN && currentUser.getRole() != Role.GLOBAL_ADMIN) {
            throw new IllegalArgumentException("Invalid target role");
        }
    }

    public void validateDelete(UUID id, CurrentUser currentUser) {
        if (id.equals(currentUser.getId())) {
            throw new IllegalArgumentException("Cannot delete yourself");
        }
    }
}
