package com.kush.todo.controller;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
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

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

//ToDo permissions tests
class AppUserControllerIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_URL = "/api/users";

    @Test
    void create() {
        AppUserRequestDto request = IntegrationTestDataBuilder.buildAppUserRequestDto();
        ResponseEntity<AppUserResponseDto> response = restTemplate.postForEntity(BASE_URL,
                                                                                 IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                 AppUserResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        assertAppUser(request, response);
    }

    @Test
    void createWithExistingUsername() {
        AppUserRequestDto request = IntegrationTestDataBuilder.buildAppUserRequestDto();
        ResponseEntity<AppUserResponseDto> successfulResponse = restTemplate.postForEntity(BASE_URL,
                                                                                           IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                           AppUserResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse.getStatusCode().value());

        AppUserRequestDto newRequest = IntegrationTestDataBuilder.buildAppUserRequestDto(request.username());
        ResponseEntity<ErrorsDto> errorResponse = restTemplate.postForEntity(BASE_URL,
                                                                             IntegrationTestDataBuilder.buildRequest(newRequest, defaultAccessToken),
                                                                             ErrorsDto.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("(username)=(%s) already exists.", request.username()),
                                errors.getFirst().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "11111111111111111111111111111111111111111111111111111"})
    void createWithInvalidUserName(String username) {
        AppUserRequestDto request = IntegrationTestDataBuilder.buildAppUserRequestDto(username);
        ResponseEntity<ErrorsDto> response = restTemplate.postForEntity(BASE_URL,
                                                                        IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                        ErrorsDto.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void get() {
        AppUserRequestDto request = IntegrationTestDataBuilder.buildAppUserRequestDto();
        ResponseEntity<AppUserResponseDto> createResponse = restTemplate.postForEntity(BASE_URL,
                                                                                       IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                       AppUserResponseDto.class);
        ResponseEntity<AppUserResponseDto> getResponse = restTemplate.exchange(BASE_URL + "/{id}",
                                                                               HttpMethod.GET,
                                                                               IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                               AppUserResponseDto.class,
                                                                               createResponse.getBody().id());

        Assertions.assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode().value());
        assertAppUser(request, getResponse);
    }

    @Test
    void me() {
        ResponseEntity<AppUserResponseDto> response = restTemplate.exchange(BASE_URL + "/me",
                                                                            HttpMethod.GET,
                                                                            IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                            AppUserResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(IntegrationTestDataBuilder.TEST_USERNAME, response.getBody().username());
    }

    @Test
    void getNotFound() {
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_URL + "/{id}",
                                                                   HttpMethod.GET,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("No user with id '%s'", absentId), errors.getFirst().message());
    }

    @Test
    void getAll() {
        AppUserRequestDto request = IntegrationTestDataBuilder.buildAppUserRequestDto();
        restTemplate.postForEntity(BASE_URL, IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken), AppUserResponseDto.class);
        ParameterizedTypeReference<CustomPage<AppUserResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<AppUserResponseDto>> getAllResponse = restTemplate.exchange(BASE_URL + "?page=1&size=10",
                                                                                              HttpMethod.GET,
                                                                                              IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                              responseType);

        Assertions.assertEquals(HttpStatus.OK.value(), getAllResponse.getStatusCode().value());
        CustomPage<AppUserResponseDto> getAllResponseBody = getAllResponse.getBody();
        Assertions.assertNotNull(getAllResponseBody);
        Assertions.assertFalse(CollectionUtils.isEmpty(getAllResponseBody.items()));
        Assertions.assertEquals(2, getAllResponseBody.items().size());
        Assertions.assertEquals(1, getAllResponseBody.totalPages());
        Assertions.assertEquals(2, getAllResponseBody.totalElements());
    }

    @Test
    void getWithInvalidId() {
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_URL + "/{id}",
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
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_URL + "?page={page}&size={size}",
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
        AppUserRequestDto createRequest = IntegrationTestDataBuilder.buildAppUserRequestDto();
        ResponseEntity<AppUserResponseDto> createResponse = restTemplate.postForEntity(BASE_URL,
                                                                                       IntegrationTestDataBuilder.buildRequest(createRequest, defaultAccessToken),
                                                                                       AppUserResponseDto.class);

        AppUserRequestDto updateRequest = IntegrationTestDataBuilder.buildAppUserRequestDto();
        ResponseEntity<AppUserResponseDto> updateResponse = restTemplate.exchange(BASE_URL + "/{id}",
                                                                                  HttpMethod.PUT,
                                                                                  IntegrationTestDataBuilder.buildRequest(updateRequest, defaultAccessToken),
                                                                                  AppUserResponseDto.class,
                                                                                  createResponse.getBody().id());

        Assertions.assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode().value());
        assertAppUser(updateRequest, updateResponse);
    }

    @Test
    void updateWithExistingName() {
        AppUserRequestDto request1 = IntegrationTestDataBuilder.buildAppUserRequestDto();
        ResponseEntity<AppUserResponseDto> successfulResponse1 = restTemplate.postForEntity(BASE_URL,
                                                                                            IntegrationTestDataBuilder.buildRequest(request1, defaultAccessToken),
                                                                                            AppUserResponseDto.class);
        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse1.getStatusCode().value());

        AppUserRequestDto request2 = IntegrationTestDataBuilder.buildAppUserRequestDto();
        ResponseEntity<AppUserResponseDto> successfulResponse2 = restTemplate.postForEntity(BASE_URL,
                                                                                            IntegrationTestDataBuilder.buildRequest(request2, defaultAccessToken),
                                                                                            AppUserResponseDto.class);
        Assertions.assertEquals(HttpStatus.CREATED.value(), successfulResponse2.getStatusCode().value());

        ResponseEntity<ErrorsDto> errorResponse = restTemplate.exchange(BASE_URL + "/{id}",
                                                                        HttpMethod.PUT,
                                                                        IntegrationTestDataBuilder.buildRequest(request1, defaultAccessToken),
                                                                        ErrorsDto.class,
                                                                        successfulResponse2.getBody().id());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("(username)=(%s) already exists.", request1.username()), errors
                .getFirst().message());
    }

    @Test
    void delete() {
        AppUserRequestDto request = IntegrationTestDataBuilder.buildAppUserRequestDto();
        String id = restTemplate.postForEntity(BASE_URL,
                                               IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                               AppUserResponseDto.class)
                                .getBody()
                                .id()
                                .toString();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(BASE_URL + "/{id}",
                                                                    HttpMethod.DELETE,
                                                                    IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                    Void.class,
                                                                    id);

        Assertions.assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode().value());
        Assertions.assertNull(deleteResponse.getBody());

        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_URL + "/{id}",
                                                                   HttpMethod.DELETE,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    void deleteNotFound() {
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_URL + "/{id}",
                                                                   HttpMethod.DELETE,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(String.format("No user with id '%s'", absentId), errors.getFirst().message());
    }

    private void assertAppUser(AppUserRequestDto request, ResponseEntity<AppUserResponseDto> response) {
        AppUserResponseDto responseBody = response.getBody();
        Assertions.assertNotNull(responseBody);
        Assertions.assertNotNull(responseBody.id());
        Assertions.assertEquals(defaultTenantId, responseBody.tenantId());
        Assertions.assertEquals(request.roleId(), responseBody.roleId());
        Assertions.assertEquals(request.username(), responseBody.username());
        Assertions.assertEquals(request.email(), responseBody.email());
        Assertions.assertEquals(request.firstname(), responseBody.firstname());
        Assertions.assertEquals(request.lastname(), responseBody.lastname());
        Assertions.assertFalse(responseBody.isLocked());
        Assertions.assertNull(responseBody.lockedAt());
        Assertions.assertNotNull(responseBody.createdAt());
        Assertions.assertNotNull(responseBody.updatedAt());
    }
}
