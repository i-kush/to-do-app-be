package com.kush.todo.controller;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.request.TenantRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.dto.response.TenantResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

class TenantControllerIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_URL = "/api/tenants";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void create() {
        TenantRequestDto tenantRequestDto = IntegrationTestDataBuilder.buildTenantRequestDto();
        ResponseEntity<TenantResponseDto> response = restTemplate.postForEntity(BASE_URL,
                                                                                IntegrationTestDataBuilder.buildRequest(tenantRequestDto),
                                                                                TenantResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().id());
        Assertions.assertEquals(tenantRequestDto.name(), response.getBody().name());
    }

    @Test
    void createWithExistingName() {
        TenantRequestDto request = IntegrationTestDataBuilder.buildTenantRequestDto();
        ResponseEntity<TenantResponseDto> successfulResponse = restTemplate.postForEntity(BASE_URL,
                                                                                          IntegrationTestDataBuilder.buildRequest(request),
                                                                                          TenantResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse.getStatusCode().value());

        ResponseEntity<ErrorsDto> errorResponse = restTemplate.postForEntity(BASE_URL,
                                                                             IntegrationTestDataBuilder.buildRequest(request),
                                                                             ErrorsDto.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("(name)=(%s) already exists.", request.name()), errors.getFirst().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "11111111111111111111111111111111111111111111111111111"})
    void createWithInvalidName(String name) {
        TenantRequestDto request = IntegrationTestDataBuilder.buildTenantRequestDto(name);
        ResponseEntity<ErrorsDto> response = restTemplate.postForEntity(BASE_URL,
                                                                        IntegrationTestDataBuilder.buildRequest(request),
                                                                        ErrorsDto.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void get() {
        TenantRequestDto request = IntegrationTestDataBuilder.buildTenantRequestDto();
        ResponseEntity<TenantResponseDto> createResponse = restTemplate.postForEntity(BASE_URL,
                                                                                      IntegrationTestDataBuilder.buildRequest(request),
                                                                                      TenantResponseDto.class);
        ResponseEntity<TenantResponseDto> getResponse = restTemplate.getForEntity(BASE_URL + "/{id}",
                                                                                  TenantResponseDto.class,
                                                                                  createResponse.getBody().id());

        Assertions.assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode().value());
        TenantResponseDto getResponseBody = getResponse.getBody();
        Assertions.assertNotNull(getResponseBody);
        Assertions.assertEquals(createResponse.getBody().id(), getResponseBody.id());
        Assertions.assertEquals(createResponse.getBody().name(), getResponseBody.name());
        Assertions.assertEquals(request.name(), getResponseBody.name());
    }

    @Test
    void getNotFound() {
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.getForEntity(BASE_URL + "/{id}",
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
        TenantRequestDto request = IntegrationTestDataBuilder.buildTenantRequestDto();
        restTemplate.postForEntity(BASE_URL, IntegrationTestDataBuilder.buildRequest(request), TenantResponseDto.class);
        ParameterizedTypeReference<CustomPage<TenantResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<TenantResponseDto>> getAllResponse = restTemplate.exchange(BASE_URL + "?page=1&size=10",
                                                                                             HttpMethod.GET,
                                                                                             null,
                                                                                             responseType);

        Assertions.assertEquals(HttpStatus.OK.value(), getAllResponse.getStatusCode().value());
        CustomPage<TenantResponseDto> getAllResponseBody = getAllResponse.getBody();
        Assertions.assertNotNull(getAllResponseBody);
        Assertions.assertFalse(CollectionUtils.isEmpty(getAllResponseBody.items()));
        Assertions.assertEquals(1, getAllResponseBody.items().size());
        Assertions.assertEquals(1, getAllResponseBody.totalPages());
        Assertions.assertEquals(1, getAllResponseBody.totalElements());
    }

    @Test
    void getWithInvalidId() {
        ResponseEntity<ErrorsDto> response = restTemplate.getForEntity(BASE_URL + "/{id}",
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
        ResponseEntity<ErrorsDto> response = restTemplate.getForEntity(BASE_URL + "?page={page}&size={size}",
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
        TenantRequestDto createRequest = IntegrationTestDataBuilder.buildTenantRequestDto();
        ResponseEntity<TenantResponseDto> createResponse = restTemplate.postForEntity(BASE_URL,
                                                                                      IntegrationTestDataBuilder.buildRequest(createRequest),
                                                                                      TenantResponseDto.class);

        TenantRequestDto updateRequest = IntegrationTestDataBuilder.buildTenantRequestDto();
        ResponseEntity<TenantResponseDto> updateResponse = restTemplate.exchange(BASE_URL + "/{id}",
                                                                                 HttpMethod.PUT,
                                                                                 IntegrationTestDataBuilder.buildRequest(updateRequest),
                                                                                 TenantResponseDto.class,
                                                                                 createResponse.getBody().id());

        Assertions.assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode().value());
        Assertions.assertNotNull(updateResponse.getBody());
        Assertions.assertNotNull(updateResponse.getBody().id());
        Assertions.assertEquals(updateRequest.name(), updateResponse.getBody().name());
        Assertions.assertNotEquals(createRequest.name(), updateResponse.getBody().name());
    }

    @Test
    void updateWithExistingName() {
        TenantRequestDto request1 = IntegrationTestDataBuilder.buildTenantRequestDto();
        ResponseEntity<TenantResponseDto> successfulResponse1 = restTemplate.postForEntity(BASE_URL,
                                                                                           IntegrationTestDataBuilder.buildRequest(request1),
                                                                                           TenantResponseDto.class);
        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse1.getStatusCode().value());

        TenantRequestDto request2 = IntegrationTestDataBuilder.buildTenantRequestDto();
        ResponseEntity<TenantResponseDto> successfulResponse2 = restTemplate.postForEntity(BASE_URL,
                                                                                           IntegrationTestDataBuilder.buildRequest(request2),
                                                                                           TenantResponseDto.class);
        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse2.getStatusCode().value());

        ResponseEntity<ErrorsDto> errorResponse = restTemplate.exchange(BASE_URL + "/{id}",
                                                                        HttpMethod.PUT,
                                                                        IntegrationTestDataBuilder.buildRequest(request1),
                                                                        ErrorsDto.class,
                                                                        successfulResponse2.getBody().id());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("(name)=(%s) already exists.", request1.name()), errors.getFirst().message());
    }

    @Test
    void delete() {
        TenantRequestDto request = IntegrationTestDataBuilder.buildTenantRequestDto();
        String id = restTemplate.postForEntity(BASE_URL,
                                               IntegrationTestDataBuilder.buildRequest(request),
                                               TenantResponseDto.class)
                                .getBody()
                                .id()
                                .toString();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(BASE_URL + "/{id}", HttpMethod.DELETE, null, Void.class, id);

        Assertions.assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode().value());
        Assertions.assertNull(deleteResponse.getBody());

        ResponseEntity<ErrorsDto> response = restTemplate.getForEntity(BASE_URL + "/{id}", ErrorsDto.class, id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    void deleteNotFound() {
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_URL + "/{id}", HttpMethod.DELETE, null, ErrorsDto.class, absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("No tenant with id '%s'", absentId), errors.getFirst().message());
    }
}
