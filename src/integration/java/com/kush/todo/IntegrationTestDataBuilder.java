package com.kush.todo;

import com.kush.todo.dto.request.LoginRequestDto;
import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.service.TenantService;

import java.util.UUID;

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

    public static TenantRequestDto buildTenantRequestDto() {
        return TenantRequestDto.builder()
                               .name(UUID.randomUUID().toString())
                               .build();
    }

    public static TenantRequestDto buildTenantRequestDto(String name) {
        return TenantRequestDto.builder()
                               .name(name)
                               .build();
    }

    public static void createTenants(TenantService tenantService, int count) {
        for (int i = 0; i < count; i++) {
            tenantService.create(buildTenantRequestDto());
        }
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

    public static LoginRequestDto buildLoginRequest() {
        return new LoginRequestDto(TEST_USERNAME, TEST_PASSWORD);
    }
}
