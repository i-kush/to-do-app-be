package com.kush.todo.mapper;

import com.kush.todo.dto.CurrentUser;
import com.kush.todo.dto.Role;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;

@Mapper
@Slf4j
public abstract class AuthMapper {

    public CurrentUser toCurrentUser(Jwt jwt) {
        return CurrentUser.builder()
                          .id(UUID.fromString(jwt.getSubject()))
                          .tenantId(UUID.fromString(jwt.getClaimAsString("tenant")))
                          .role(Role.valueOf(jwt.getClaimAsString("role")))
                          .username(jwt.getClaimAsString("username"))
                          .email(jwt.getClaimAsString("email"))
                          .build();
    }
}
