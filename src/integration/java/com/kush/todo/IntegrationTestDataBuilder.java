package com.kush.todo;

import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.service.TenantService;

import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public final class IntegrationTestDataBuilder {

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

    @SuppressWarnings("PMD.LooseCoupling")
    public static <T> HttpEntity<T> buildRequest(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }
}
