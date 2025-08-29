package com.kush.todo.mapper;

import com.kush.todo.dto.CurrentUser;
import com.kush.todo.dto.Permission;
import com.kush.todo.dto.Role;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public String toScope(List<Permission> userPermission) {
        return userPermission.stream()
                             .map(Permission::toString)
                             .collect(Collectors.joining(" "));
    }

    public CurrentUser buildCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                       .map(org.springframework.security.core.context.SecurityContext::getAuthentication)
                       .map(Authentication::getPrincipal)
                       .filter(org.springframework.security.oauth2.jwt.Jwt.class::isInstance)
                       .map(org.springframework.security.oauth2.jwt.Jwt.class::cast)
                       .map(this::toCurrentUser)
                       .orElseThrow(() -> new IllegalStateException("No current user detected"));
    }
}
