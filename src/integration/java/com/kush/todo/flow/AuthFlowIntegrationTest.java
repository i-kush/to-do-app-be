package com.kush.todo.flow;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.TenantResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AuthFlowIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_URL = "/api/tenants";

    @ParameterizedTest
    @ValueSource(strings = {
            "  ",
            " ",
            "invalid",
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxOWFkMWQzMi1jOGEzLTQ5NDgtYTU1ZC0yZTQ1YTIxZDM4NjgiLCJyb2xlIjoiR0xPQkFMX0FETUlOIiwic2NvcGUiOiJSRUFEIFdSSVRFIiwiaXNzIjoidG9kby1iYWNrZW5kIiwiZXhwIjoxNzU2MzE5NzE2LCJpYXQiOjE3NTYzMTk0MTYsInRlbmFudCI6ImE1ZDg4Mzg2LTgyNjQtNDE2NS05MDMxLWQ1ZTdhMWVlNDhlOCIsImVtYWlsIjoic3lzdGVtLWFkbWluQGt1c2gtdG8tZG8uY29tIiwidXNlcm5hbWUiOiJnbG9iYWwtYWRtaW4ifQ.Xj8i3rpyBiKPo3YwNoI3wgAaaY71aynHe9JIQwtdQGo"
    })
    @NullAndEmptySource
    void unauthorized(String token) {
        TenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildTenantRequestDto();
        ResponseEntity<TenantResponseDto> response = restTemplate.postForEntity(BASE_URL,
                                                                                IntegrationTestDataBuilder.buildRequest(tenantRequestDto, token),
                                                                                TenantResponseDto.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCode().value());
    }
}
