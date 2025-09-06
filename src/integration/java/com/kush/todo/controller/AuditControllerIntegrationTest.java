package com.kush.todo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.common.AuditActionResult;
import com.kush.todo.dto.common.AuditActionType;
import com.kush.todo.dto.common.AuditTargetType;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.response.AuditResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.dto.response.TenantDetailsResponseDto;
import com.kush.todo.dto.response.TenantResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

class AuditControllerIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_TENANT_URL = "/api/tenants";
    public static final String BASE_AUDIT_URL = "/api/audit";

    @Test
    void getAll() {
        CreateTenantRequestDto request = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<TenantResponseDto> tenantCreationResponse = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                                         HttpMethod.GET,
                                                                                         IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                         TenantResponseDto.class,
                                                                                         defaultTenantId);
        Assertions.assertEquals(HttpStatus.OK.value(), tenantCreationResponse.getStatusCode().value());
        TenantResponseDto createdTenant = tenantCreationResponse.getBody();
        Assertions.assertNotNull(createdTenant);

        ParameterizedTypeReference<CustomPage<AuditResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<AuditResponseDto>> getAllResponse = restTemplate.exchange(BASE_AUDIT_URL + "?page=1&size=10",
                                                                                            HttpMethod.GET,
                                                                                            IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                            responseType);

        Assertions.assertEquals(HttpStatus.OK.value(), getAllResponse.getStatusCode().value());
        CustomPage<AuditResponseDto> getAllResponseBody = getAllResponse.getBody();
        Assertions.assertNotNull(getAllResponseBody);
        List<AuditResponseDto> items = getAllResponseBody.items();
        Assertions.assertFalse(CollectionUtils.isEmpty(items));
        int expectedTotalElements = 1; //2 created by the tests + 1 'system' tenant
        Assertions.assertEquals(expectedTotalElements, items.size());
        Assertions.assertEquals(1, getAllResponseBody.totalPages());
        Assertions.assertEquals(expectedTotalElements, getAllResponseBody.totalElements());

        items.sort(Comparator.comparing(AuditResponseDto::createdAt));
        AuditResponseDto auditResponseDto = items.getFirst();

        Assertions.assertNotNull(auditResponseDto);
        Assertions.assertNotNull(auditResponseDto.id());
        Assertions.assertEquals(defaultTenantId, auditResponseDto.tenantId());
        Assertions.assertEquals(defaultUserId, auditResponseDto.initiatorId());
        Assertions.assertEquals(createdTenant.id(), auditResponseDto.targetId());
        Assertions.assertEquals(AuditTargetType.TENANT, auditResponseDto.targetType());
        Assertions.assertEquals(AuditActionType.READ, auditResponseDto.actionType());
        Assertions.assertEquals(AuditActionResult.SUCCESS, auditResponseDto.actionResult());
        Assertions.assertNotNull(auditResponseDto.createdAt());
        Assertions.assertNotNull(auditResponseDto.spanId());
        Assertions.assertNotNull(auditResponseDto.traceId());
        Assertions.assertNull(auditResponseDto.details());
    }

    @Test
    void getAllWithFailedOperation() {
        CreateTenantRequestDto request = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<TenantDetailsResponseDto> successfulResponse = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                                                 IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                                 TenantDetailsResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse.getStatusCode().value());

        ResponseEntity<ErrorsDto> errorResponse = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                             IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                             ErrorsDto.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("(name)=(%s) already exists.", request.name()), errors.getFirst().message());

        ParameterizedTypeReference<CustomPage<AuditResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<AuditResponseDto>> getAllResponse = restTemplate.exchange(BASE_AUDIT_URL + "?page=1&size=10",
                                                                                            HttpMethod.GET,
                                                                                            IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                            responseType);

        Assertions.assertEquals(HttpStatus.OK.value(), getAllResponse.getStatusCode().value());
        CustomPage<AuditResponseDto> getAllResponseBody = getAllResponse.getBody();
        Assertions.assertNotNull(getAllResponseBody);
        List<AuditResponseDto> items = getAllResponseBody.items();
        Assertions.assertFalse(CollectionUtils.isEmpty(items));
        int expectedTotalElements = 2; //1 successfully created tenant, 1 error for tenant creation
        Assertions.assertEquals(expectedTotalElements, items.size());
        Assertions.assertEquals(1, getAllResponseBody.totalPages());
        Assertions.assertEquals(expectedTotalElements, getAllResponseBody.totalElements());

        AuditResponseDto auditResponseDto = items.getLast();
        Assertions.assertNotNull(auditResponseDto);
        Assertions.assertNotNull(auditResponseDto.id());
        Assertions.assertEquals(defaultTenantId, auditResponseDto.tenantId());
        Assertions.assertEquals(defaultUserId, auditResponseDto.initiatorId());
        Assertions.assertNull(auditResponseDto.targetId()); //tenant does not have an ID yet
        Assertions.assertEquals(AuditTargetType.TENANT, auditResponseDto.targetType());
        Assertions.assertEquals(AuditActionType.CREATE, auditResponseDto.actionType());
        Assertions.assertEquals(AuditActionResult.FAILURE, auditResponseDto.actionResult());
        Assertions.assertNotNull(auditResponseDto.createdAt());
        Assertions.assertNotNull(auditResponseDto.spanId());
        Assertions.assertNotNull(auditResponseDto.traceId());

        JsonNode details = auditResponseDto.details();
        Assertions.assertNotNull(details);
        Assertions.assertFalse(details.get("type").isNull());
        Assertions.assertFalse(details.get("message").isNull());
    }
}
