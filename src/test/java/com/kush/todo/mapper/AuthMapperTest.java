package com.kush.todo.mapper;

import com.kush.todo.TestDataBuilder;
import com.kush.todo.dto.common.CurrentUser;
import com.kush.todo.dto.common.Permission;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;

class AuthMapperTest {

    private final AuthMapper authMapper = Mappers.getMapper(AuthMapper.class);

    @Test
    void toCurrentUser() {
        Jwt jwt = TestDataBuilder.buildJwt();

        CurrentUser actual = authMapper.toCurrentUser(jwt);

        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(jwt.getSubject(), actual.getId().toString());
        Assertions.assertNotNull(actual.getTenantId());
        Assertions.assertEquals(jwt.getClaimAsString("tenant"), actual.getTenantId().toString());
        Assertions.assertNotNull(actual.getRole());
        Assertions.assertEquals(jwt.getClaimAsString("role"), actual.getRole().toString());
        Assertions.assertNotNull(actual.getUsername());
        Assertions.assertEquals(jwt.getClaimAsString("username"), actual.getUsername());
        Assertions.assertNotNull(actual.getEmail());
        Assertions.assertEquals(jwt.getClaimAsString("email"), actual.getEmail());
    }

    @Test
    void toScope() {
        List<Permission> permissions = List.of(Permission.PROJECT_WRITE, Permission.TENANT_WRITE, Permission.TASK_WRITE);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < permissions.size(); i++) {
            stringBuilder.append(permissions.get(i));
            if (i != permissions.size() - 1) {
                stringBuilder.append(' ');
            }
        }

        String actual = authMapper.toScope(permissions);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(stringBuilder.toString(), actual);
    }

    @Test
    void buildCurrentUser() {
        Assertions.assertThrows(IllegalStateException.class, authMapper::buildCurrentUser);
    }
}