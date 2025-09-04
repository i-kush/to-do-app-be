package com.kush.todo.controller;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.async.AsyncOperationStatus;
import com.kush.todo.dto.common.Role;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.dto.request.UpdateTenantRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.AsyncOperationQueuedResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.dto.response.TenantDetailsResponseDto;
import com.kush.todo.dto.response.TenantResponseDto;
import com.kush.todo.mapper.AppUserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

@SuppressWarnings("PMD.CouplingBetweenObjects")
class TenantControllerIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_TENANT_URL = "/api/tenants";
    public static final String BASE_ASYNC_URL = "/api/async/operations";

    @Test
    void create() {
        CreateTenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<TenantDetailsResponseDto> response = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                                       IntegrationTestDataBuilder.buildRequest(tenantRequestDto, defaultAccessToken),
                                                                                       TenantDetailsResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());

        assertTenant(response.getBody(), tenantRequestDto);
    }

    private void assertTenant(TenantDetailsResponseDto response, CreateTenantRequestDto request) {
        TenantResponseDto tenantResponseDto = response.tenant();
        Assertions.assertNotNull(tenantResponseDto);
        Assertions.assertNotNull(tenantResponseDto.id());
        Assertions.assertEquals(request.name(), tenantResponseDto.name());

        Set<AppUserResponseDto> appUserResponseDtos = response.admins();
        Assertions.assertFalse(CollectionUtils.isEmpty(appUserResponseDtos));
        AppUserResponseDto appUserResponseDto = appUserResponseDtos.iterator().next();
        Assertions.assertNotNull(appUserResponseDto);
        Assertions.assertNotNull(appUserResponseDto.id());
        Assertions.assertEquals(request.adminEmail(), appUserResponseDto.username());
        Assertions.assertEquals(request.adminEmail(), appUserResponseDto.email());
        Assertions.assertEquals(AppUserMapper.INITIAL_ADMIN_FIRST_NAME, appUserResponseDto.firstname());
        Assertions.assertEquals(AppUserMapper.INITIAL_ADMIN_LAST_NAME, appUserResponseDto.lastname());
        Assertions.assertEquals(Role.TENANT_ADMIN, appUserResponseDto.roleId());
        Assertions.assertNotNull(appUserResponseDto.createdAt());
        Assertions.assertNotNull(appUserResponseDto.updatedAt());

        boolean exists = jdbcTemplate.queryForObject("select exists(select id from app_user where id = ?)", Boolean.class, appUserResponseDto.id());
        Assertions.assertTrue(exists);
    }

    @Test
    void createWithExistingName() {
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
    }

    @Test
    void createAsync() {
        CreateTenantRequestDto asyncTenantRequestDto = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<AsyncOperationQueuedResponseDto> asyncResponse = restTemplate.postForEntity(BASE_TENANT_URL + "/async",
                                                                                                   IntegrationTestDataBuilder.buildRequest(asyncTenantRequestDto, defaultAccessToken),
                                                                                                   AsyncOperationQueuedResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK.value(), asyncResponse.getStatusCode().value());
        Assertions.assertNotNull(asyncResponse.getBody());

        UUID operationId = asyncResponse.getBody().id();
        Assertions.assertNotNull(operationId);

        ParameterizedTypeReference<AsyncOperationDto<TenantDetailsResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<AsyncOperationDto<TenantDetailsResponseDto>> asyncResponseResult = null;
        for (int i = 0; i < 5; i++) {
            asyncResponseResult = restTemplate.exchange(BASE_ASYNC_URL + "/{id}",
                                                        HttpMethod.GET,
                                                        IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                        responseType,
                                                        operationId);
            Assertions.assertEquals(HttpStatus.OK.value(), asyncResponseResult.getStatusCode().value());
            AsyncOperationDto<TenantDetailsResponseDto> asyncResponseResultBody = asyncResponseResult.getBody();
            Assertions.assertNotNull(asyncResponseResultBody);
            if (asyncResponseResultBody.status() == AsyncOperationStatus.SUCCESS) {
                break;
            }
            pause(2);
        }

        Assertions.assertNotNull(asyncResponseResult);
        Assertions.assertEquals(HttpStatus.OK.value(), asyncResponseResult.getStatusCode().value());
        AsyncOperationDto<TenantDetailsResponseDto> asyncResponseResultBody = asyncResponseResult.getBody();
        Assertions.assertNotNull(asyncResponseResultBody);
        Assertions.assertEquals(operationId, asyncResponseResultBody.id());
        Assertions.assertEquals(defaultTenantId, asyncResponseResultBody.tenantId());
        Assertions.assertEquals(AsyncOperationStatus.SUCCESS, asyncResponseResultBody.status());

        TenantDetailsResponseDto asyncTenantResponseDto = asyncResponseResultBody.result();

        assertTenant(asyncTenantResponseDto, asyncTenantRequestDto);
    }

    @Test
    void createAsyncWithExistingName() {
        CreateTenantRequestDto request = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<TenantResponseDto> successfulResponse = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                                          IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                          TenantResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse.getStatusCode().value());

        ResponseEntity<AsyncOperationQueuedResponseDto> asyncResponse = restTemplate.postForEntity(BASE_TENANT_URL + "/async",
                                                                                                   IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                                   AsyncOperationQueuedResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK.value(), asyncResponse.getStatusCode().value());
        Assertions.assertNotNull(asyncResponse.getBody());

        UUID operationId = asyncResponse.getBody().id();
        Assertions.assertNotNull(operationId);

        ParameterizedTypeReference<AsyncOperationDto<TenantResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<AsyncOperationDto<TenantResponseDto>> asyncResponseResult = null;
        for (int i = 0; i < 5; i++) {
            asyncResponseResult = restTemplate.exchange(BASE_ASYNC_URL + "/{id}",
                                                        HttpMethod.GET,
                                                        IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                        responseType,
                                                        operationId);
            Assertions.assertEquals(HttpStatus.OK.value(), asyncResponseResult.getStatusCode().value());
            AsyncOperationDto<TenantResponseDto> asyncResponseResultBody = asyncResponseResult.getBody();
            Assertions.assertNotNull(asyncResponseResultBody);
            if (asyncResponseResultBody.status() == AsyncOperationStatus.ERROR) {
                break;
            }
            pause(2);
        }

        Assertions.assertNotNull(asyncResponseResult);
        Assertions.assertEquals(HttpStatus.OK.value(), asyncResponseResult.getStatusCode().value());
        AsyncOperationDto<TenantResponseDto> asyncResponseResultBody = asyncResponseResult.getBody();
        Assertions.assertNotNull(asyncResponseResultBody);
        Assertions.assertEquals(operationId, asyncResponseResultBody.id());
        Assertions.assertEquals(defaultTenantId, asyncResponseResultBody.tenantId());
        Assertions.assertEquals(AsyncOperationStatus.ERROR, asyncResponseResultBody.status());
        Assertions.assertNull(asyncResponseResultBody.result());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "11111111111111111111111111111111111111111111111111111"})
    void createWithInvalidName(String name) {
        CreateTenantRequestDto request = IntegrationTestDataBuilder.buildCreateTenantRequestDtoByName(name);
        ResponseEntity<ErrorsDto> response = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                        IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                        ErrorsDto.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "11111111111111111111111111111111111111111111111111111"})
    void createWithInvalidAdminEmail(String adminEmail) {
        CreateTenantRequestDto request = IntegrationTestDataBuilder.buildCreateTenantRequestDtoByEmail(adminEmail);
        ResponseEntity<ErrorsDto> response = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                        IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                        ErrorsDto.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void get() {
        CreateTenantRequestDto request = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<TenantDetailsResponseDto> createResponse = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                                             IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                             TenantDetailsResponseDto.class);
        ResponseEntity<TenantResponseDto> getResponse = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                              HttpMethod.GET,
                                                                              IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                              TenantResponseDto.class,
                                                                              createResponse.getBody().tenant().id());

        Assertions.assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode().value());
        TenantResponseDto getResponseBody = getResponse.getBody();
        Assertions.assertNotNull(getResponseBody);
        Assertions.assertEquals(createResponse.getBody().tenant().id(), getResponseBody.id());
        Assertions.assertEquals(createResponse.getBody().tenant().name(), getResponseBody.name());
        Assertions.assertEquals(request.name(), getResponseBody.name());
    }

    @Test
    void getNotFound() {
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                   HttpMethod.GET,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("No tenant with id '%s'", absentId), errors.getFirst().message());
    }

    @Test
    void getAll() {
        CreateTenantRequestDto request = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        restTemplate.postForEntity(BASE_TENANT_URL, IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken), TenantResponseDto.class);
        ParameterizedTypeReference<CustomPage<TenantResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<TenantResponseDto>> getAllResponse = restTemplate.exchange(BASE_TENANT_URL + "?page=1&size=10",
                                                                                             HttpMethod.GET,
                                                                                             IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                             responseType);

        Assertions.assertEquals(HttpStatus.OK.value(), getAllResponse.getStatusCode().value());
        CustomPage<TenantResponseDto> getAllResponseBody = getAllResponse.getBody();
        Assertions.assertNotNull(getAllResponseBody);
        Assertions.assertFalse(CollectionUtils.isEmpty(getAllResponseBody.items()));
        int expectedTotalElements = 3; //2 created by the tests + 1 'system' tenant
        Assertions.assertEquals(expectedTotalElements, getAllResponseBody.items().size());
        Assertions.assertEquals(1, getAllResponseBody.totalPages());
        Assertions.assertEquals(expectedTotalElements, getAllResponseBody.totalElements());
    }

    @Test
    void getWithInvalidId() {
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                   HttpMethod.GET,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   "test");

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals("Invalid type for 'id'", errors.getFirst().message());
    }

    @ParameterizedTest
    @MethodSource("getAllWIthInvalidPageArgs")
    void getAllWIthInvalidPage(int page, int size, String message) {
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_TENANT_URL + "?page={page}&size={size}",
                                                                   HttpMethod.GET,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   page,
                                                                   size);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(message, errors.getFirst().message());
    }

    private static Stream<Arguments> getAllWIthInvalidPageArgs() {
        return Stream.of(
                Arguments.of(0, 1, "must be greater than or equal to 1"),
                Arguments.of(1, 0, "must be greater than or equal to 1"),
                Arguments.of(2, 201, "must be less than or equal to 200")
        );
    }

    @Test
    void update() {
        CreateTenantRequestDto createRequest = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<TenantDetailsResponseDto> createResponse = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                                             IntegrationTestDataBuilder.buildRequest(createRequest, defaultAccessToken),
                                                                                             TenantDetailsResponseDto.class);

        UpdateTenantRequestDto updateRequest = IntegrationTestDataBuilder.buildUpdateTenantRequestDto();
        ResponseEntity<TenantResponseDto> updateResponse = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                                 HttpMethod.PUT,
                                                                                 IntegrationTestDataBuilder.buildRequest(updateRequest, defaultAccessToken),
                                                                                 TenantResponseDto.class,
                                                                                 createResponse.getBody().tenant().id());

        Assertions.assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode().value());
        Assertions.assertNotNull(updateResponse.getBody());
        Assertions.assertNotNull(updateResponse.getBody().id());
        Assertions.assertEquals(updateRequest.name(), updateResponse.getBody().name());
        Assertions.assertNotEquals(createRequest.name(), updateResponse.getBody().name());
    }

    @Test
    void updateWithExistingName() {
        CreateTenantRequestDto request1 = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<TenantDetailsResponseDto> successfulResponse1 = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                                                  IntegrationTestDataBuilder.buildRequest(request1, defaultAccessToken),
                                                                                                  TenantDetailsResponseDto.class);
        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse1.getStatusCode().value());

        CreateTenantRequestDto request2 = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        ResponseEntity<TenantDetailsResponseDto> successfulResponse2 = restTemplate.postForEntity(BASE_TENANT_URL,
                                                                                                  IntegrationTestDataBuilder.buildRequest(request2, defaultAccessToken),
                                                                                                  TenantDetailsResponseDto.class);
        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse2.getStatusCode().value());

        ResponseEntity<ErrorsDto> errorResponse = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                        HttpMethod.PUT,
                                                                        IntegrationTestDataBuilder.buildRequest(request1, defaultAccessToken),
                                                                        ErrorsDto.class,
                                                                        successfulResponse2.getBody().tenant().id());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("(name)=(%s) already exists.", request1.name()), errors.getFirst().message());
    }

    @Test
    @Disabled("Will be enabled once offboarding is implemented in https://github.com/i-kush/to-do-app-be/issues/34")
    void delete() {
        CreateTenantRequestDto request = IntegrationTestDataBuilder.buildCreateTenantRequestDto();
        String id = restTemplate.postForEntity(BASE_TENANT_URL,
                                               IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                               TenantDetailsResponseDto.class)
                                .getBody()
                                .tenant()
                                .id()
                                .toString();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                    HttpMethod.DELETE,
                                                                    IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                    Void.class,
                                                                    id);

        Assertions.assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode().value());
        Assertions.assertNull(deleteResponse.getBody());

        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                   HttpMethod.DELETE,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    void deleteNotFound() {
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                   HttpMethod.DELETE,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("No tenant with id '%s'", absentId), errors.getFirst().message());
    }

    @Test
    void deleteSystemTenantDenied() {
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_TENANT_URL + "/{id}",
                                                                   HttpMethod.DELETE,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   systemTenantId);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("Tenant '%s' cannot be deleted", systemTenantId), errors.getFirst().message());
    }
}
