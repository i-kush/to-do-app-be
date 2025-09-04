package com.kush.todo;

import com.kush.todo.dto.common.Role;
import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.request.LoginRequestDto;
import com.kush.todo.dto.request.UpdateTenantRequestDto;
import com.kush.todo.entity.AppUser;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public final class IntegrationTestDataBuilder {

    public static final String TEST_PASSWORD = "password";
    public static final String TEST_USERNAME = "testenko";

    private IntegrationTestDataBuilder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static UpdateTenantRequestDto buildUpdateTenantRequestDto() {
        return UpdateTenantRequestDto.builder()
                                     .name(UUID.randomUUID().toString())
                                     .build();
    }

    public static CreateTenantRequestDto buildCreateTenantRequestDto() {
        return CreateTenantRequestDto.builder()
                                     .name(UUID.randomUUID().toString())
                                     .adminEmail(buildEmail())
                                     .build();
    }

    public static CreateTenantRequestDto buildCreateTenantRequestDtoByEmail(String adminEmail) {
        return CreateTenantRequestDto.builder()
                                     .name(UUID.randomUUID().toString())
                                     .adminEmail(adminEmail)
                                     .build();
    }

    public static CreateTenantRequestDto buildCreateTenantRequestDtoByName(String name) {
        return CreateTenantRequestDto.builder()
                                     .name(name)
                                     .adminEmail(buildEmail())
                                     .build();
    }

    public static AppUserRequestDto buildAppUserRequestDto() {
        return buildAppUserRequestDto("u-" + ThreadLocalRandom.current().nextInt(1_000_000));
    }

    public static AppUserRequestDto buildAppUserRequestDto(String username) {
        return AppUserRequestDto.builder()
                                .username(username)
                                .password("p-" + ThreadLocalRandom.current().nextInt(1_000_000))
                                .roleId(Role.TENANT_ADMIN)
                                .email(buildEmail())
                                .firstname("firstname-" + UUID.randomUUID())
                                .lastname("lastname-" + UUID.randomUUID())
                                .build();
    }

    public static AppUserRequestDto buildAppUserRequestDto(Role role) {
        return AppUserRequestDto.builder()
                                .username("u-" + ThreadLocalRandom.current().nextInt(1_000_000))
                                .password("p-" + ThreadLocalRandom.current().nextInt(1_000_000))
                                .roleId(role)
                                .email(UUID.randomUUID() + "@email.com")
                                .firstname("firstname-" + UUID.randomUUID())
                                .lastname("lastname-" + UUID.randomUUID())
                                .build();
    }

    public static AppUser buildLockedAppUser(Instant lockedAt, UUID tenantId) {
        return AppUser.builder()
                      .username("u-" + ThreadLocalRandom.current().nextInt(1_000_000))
                      .tenantId(tenantId)
                      .passwordHash("p-" + ThreadLocalRandom.current().nextInt(1_000_000))
                      .roleId(Role.TENANT_ADMIN)
                      .email(buildEmail())
                      .firstname("firstname-" + UUID.randomUUID())
                      .lastname("lastname-" + UUID.randomUUID())
                      .createdAt(Instant.now())
                      .updatedAt(Instant.now())
                      .lockedAt(lockedAt)
                      .loginAttempts(1)
                      .lastLoginAttemptAt(Instant.now())
                      .isLocked(true)
                      .build();
    }

    private static String buildEmail() {
        return UUID.randomUUID() + "@email.com";
    }

    public static <T> HttpEntity<T> buildRequest(String accessToken) {
        return buildRequest(null, accessToken);
    }

    @SuppressWarnings("PMD.LooseCoupling")
    public static <T> HttpEntity<T> buildRequest(T body, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        if (body != null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        if (StringUtils.hasText(accessToken)) {
            headers.setBearerAuth(accessToken);
        }

        return new HttpEntity<>(body, headers);
    }

    public static LoginRequestDto buildDefaultLoginRequest() {
        return buildLoginRequest(TEST_USERNAME, TEST_PASSWORD);
    }

    public static LoginRequestDto buildLoginRequest(String username, String password) {
        return LoginRequestDto.builder()
                              .username(username)
                              .password(password)
                              .build();
    }
}
