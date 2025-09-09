package com.kush.todo.flow;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.constant.CommonErrorMessages;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.request.LoginRequestDto;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

class AuthFlowIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_URL_TENANT = "/api/tenants";
    public static final String BASE_URL_LOGIN = "/api/auth/login";

    @Value("${todo.login.max-attempts}")
    private int maxAttempts;

    @ParameterizedTest
    @ValueSource(strings = {
            "  ",
            " ",
            "invalid",
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxOWFkMWQzMi1jOGEzLTQ5NDgtYTU1ZC0yZTQ1YTIxZDM4NjgiLCJyb2xlIjoiR0xPQkFMX0FETUlOIiwic2NvcGUiOiJSRUFEIFdSSVRFIiwiaXNzIjoidG9kby1iYWNrZW5kIiwiZXhwIjoxNzU2MzE5NzE2LCJpYXQiOjE3NTYzMTk0MTYsInRlbmFudCI6ImE1ZDg4Mzg2LTgyNjQtNDE2NS05MDMxLWQ1ZTdhMWVlNDhlOCIsImVtYWlsIjoic3lzdGVtLWFkbWluQGt1c2gtdG8tZG8uY29tIiwidXNlcm5hbWUiOiJnbG9iYWwtYWRtaW4ifQ.Xj8i3rpyBiKPo3YwNoI3wgAaaY71aynHe9JIQwtdQGo"
    })
    @NullAndEmptySource
    void unauthorized(String token) {
        CreateTenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<ErrorsDto> response = restTemplate.postForEntity(BASE_URL_TENANT,
                                                                        IntegrationTestDataBuilder.buildRequest(tenantRequestDto, token),
                                                                        ErrorsDto.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }

    @Test
    void unauthorizedIfUserIsLocked() {
        LoginRequestDto request = IntegrationTestDataBuilder.buildLoginRequest(IntegrationTestDataBuilder.TEST_USERNAME,
                                                                               UUID.randomUUID().toString());
        ResponseEntity<ErrorsDto> errorResponse;
        for (int i = 0; i < maxAttempts; i++) {
            errorResponse = restTemplate.postForEntity(BASE_URL_LOGIN, request, ErrorsDto.class);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatusCode().value());
            Assertions.assertNotNull(errorResponse.getBody());
            List<ErrorDto> errors = errorResponse.getBody().errors();
            Assertions.assertFalse(CollectionUtils.isEmpty(errors));
            Assertions.assertEquals(1, errors.size());
            Assertions.assertEquals(CommonErrorMessages.USER_INVALID_CREDS, errors.getFirst().message());
        }

        errorResponse = restTemplate.postForEntity(BASE_URL_LOGIN, request, ErrorsDto.class);
        assertLockedError(errorResponse);

        CreateTenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<ErrorsDto> tenantErrorResponse = restTemplate.postForEntity(BASE_URL_TENANT,
                                                                                   IntegrationTestDataBuilder.buildRequest(tenantRequestDto, defaultAccessToken),
                                                                                   ErrorsDto.class);
        assertLockedError(tenantErrorResponse);
    }

    private void assertLockedError(ResponseEntity<ErrorsDto> errorResponse) {
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals(CommonErrorMessages.USER_LOCKED, errors.getFirst().message());
    }
}
