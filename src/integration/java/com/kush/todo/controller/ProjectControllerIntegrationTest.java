package com.kush.todo.controller;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.ProjectStatus;
import com.kush.todo.dto.request.ProjectRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.dto.response.ProjectResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

class ProjectControllerIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_PROJECT_URL = "/api/projects";

    @Test
    void create() {
        ProjectRequestDto request = IntegrationTestDataBuilder.buildProjectRequestDto();
        ResponseEntity<ProjectResponseDto> response = restTemplate.postForEntity(BASE_PROJECT_URL,
                                                                                 IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                 ProjectResponseDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        assertProject(response.getBody(), request);
    }

    private void assertProject(ProjectResponseDto response, ProjectRequestDto request) {
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        Assertions.assertEquals(request.name(), response.name());
        Assertions.assertEquals(defaultTenantId, response.tenantId());
        Assertions.assertEquals(request.name(), response.name());
        Assertions.assertEquals(request.description(), response.description());
        Assertions.assertNotNull(response.status());
        Assertions.assertNotNull(response.createdAt());
        Assertions.assertNotNull(response.updatedAt());
    }


    @Test
    public void createWithExistingName() {
        ProjectRequestDto request = IntegrationTestDataBuilder.buildProjectRequestDto();
        restTemplate.postForEntity(BASE_PROJECT_URL,
                                   IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                   ProjectResponseDto.class);

        ResponseEntity<ErrorsDto> errorResponse = restTemplate.postForEntity(BASE_PROJECT_URL,
                                                                             IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                             ErrorsDto.class
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertTrue(errors.getFirst().message().contains("already exists"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"})
    void createWithInvalidName(String name) {
        ProjectRequestDto request = IntegrationTestDataBuilder.buildProjectRequestDto(name);
        ResponseEntity<ErrorsDto> response = restTemplate.postForEntity(
                BASE_PROJECT_URL,
                IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                ErrorsDto.class
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "1", "111111111111111111111111111111111111"})
    void searchWithInvalidKey(String key) {
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(
                BASE_PROJECT_URL + "/search?page=1&size=10&key={key}",
                HttpMethod.GET,
                IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                ErrorsDto.class,
                key
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void get() {
        ProjectRequestDto request = IntegrationTestDataBuilder.buildProjectRequestDto();
        ResponseEntity<ProjectResponseDto> createResponse = restTemplate.postForEntity(BASE_PROJECT_URL,
                                                                                       IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                                       ProjectResponseDto.class);
        UUID id = createResponse.getBody().id();
        ResponseEntity<ProjectResponseDto> getResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{id}",
                                                                               HttpMethod.GET,
                                                                               IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                               ProjectResponseDto.class,
                                                                               id);

        Assertions.assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode().value());
        ProjectResponseDto getResponseBody = getResponse.getBody();
        Assertions.assertNotNull(getResponseBody);
        Assertions.assertEquals(id, getResponseBody.id());
        Assertions.assertEquals(request.name(), getResponseBody.name());
    }

    @Test
    void getNotFound() {
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_PROJECT_URL + "/{id}",
                                                                   HttpMethod.GET,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
    }

    @Test
    void getAll() {
        ProjectRequestDto request = IntegrationTestDataBuilder.buildProjectRequestDto();
        restTemplate.postForEntity(BASE_PROJECT_URL,
                                   IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                   ProjectResponseDto.class);

        ParameterizedTypeReference<CustomPage<ProjectResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<ProjectResponseDto>> getAllResponse = restTemplate.exchange(BASE_PROJECT_URL + "?page=1&size=10",
                                                                                              HttpMethod.GET,
                                                                                              IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                              responseType);

        Assertions.assertEquals(HttpStatus.OK.value(), getAllResponse.getStatusCode().value());
        CustomPage<ProjectResponseDto> getAllResponseBody = getAllResponse.getBody();
        Assertions.assertNotNull(getAllResponseBody);
        Assertions.assertFalse(CollectionUtils.isEmpty(getAllResponseBody.items()));
        Assertions.assertEquals(1, getAllResponseBody.totalPages());
        Assertions.assertEquals(1, getAllResponseBody.totalElements());
    }

    @Test
    void search() {
        String key = "UniquE-NamE-ParT";
        restTemplate.postForEntity(
                BASE_PROJECT_URL,
                IntegrationTestDataBuilder.buildRequest(IntegrationTestDataBuilder.buildProjectRequestDto(key.toLowerCase(Locale.getDefault()) + UUID.randomUUID()), defaultAccessToken),
                ProjectResponseDto.class);
        restTemplate.postForEntity(
                BASE_PROJECT_URL,
                IntegrationTestDataBuilder.buildRequest(IntegrationTestDataBuilder.buildProjectRequestDto(key.toUpperCase(Locale.getDefault()) + UUID.randomUUID()), defaultAccessToken),
                ProjectResponseDto.class);
        restTemplate.postForEntity(
                BASE_PROJECT_URL,
                IntegrationTestDataBuilder.buildRequest(IntegrationTestDataBuilder.buildProjectRequestDto(), defaultAccessToken),
                ProjectResponseDto.class);

        ParameterizedTypeReference<CustomPage<ProjectResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<ProjectResponseDto>> searchAllResponce = restTemplate.exchange(BASE_PROJECT_URL + "/search?page=1&size=10&key={key}",
                                                                                                 HttpMethod.GET,
                                                                                                 IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                                 responseType,
                                                                                                 key);

        Assertions.assertEquals(HttpStatus.OK.value(), searchAllResponce.getStatusCode().value());
        CustomPage<ProjectResponseDto> searchAllResponseBody = searchAllResponce.getBody();
        Assertions.assertNotNull(searchAllResponseBody);
        List<ProjectResponseDto> items = searchAllResponseBody.items();
        Assertions.assertFalse(CollectionUtils.isEmpty(items));
        Assertions.assertEquals(1, searchAllResponseBody.totalPages());
        Assertions.assertEquals(2, searchAllResponseBody.totalElements());
        items.forEach(item -> Assertions.assertTrue(
                item.name().toLowerCase(Locale.getDefault()).contains(key.toLowerCase(Locale.getDefault()))));
    }

    @ParameterizedTest
    @MethodSource("getAllWithInvalidPageArgs")
    void getAllProjectsWithInvalidPage(int page, int size, String message) {
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_PROJECT_URL + "?page={page}&size={size}",
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

    private static Stream<Arguments> getAllWithInvalidPageArgs() {
        return Stream.of(
                Arguments.of(0, 1, "must be greater than or equal to 1"),
                Arguments.of(1, 0, "must be greater than or equal to 1"),
                Arguments.of(2, 201, "must be less than or equal to 200")
        );
    }

    @Test
    void update() {
        ProjectRequestDto createRequest = IntegrationTestDataBuilder.buildProjectRequestDto();
        ResponseEntity<ProjectResponseDto> createResponse = restTemplate.postForEntity(BASE_PROJECT_URL,
                                                                                       IntegrationTestDataBuilder.buildRequest(createRequest, defaultAccessToken),
                                                                                       ProjectResponseDto.class);
        UUID id = createResponse.getBody().id();

        ProjectRequestDto updateRequest = IntegrationTestDataBuilder.buildProjectRequestDto();
        ResponseEntity<ProjectResponseDto> updateResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{id}",
                                                                                  HttpMethod.PUT,
                                                                                  IntegrationTestDataBuilder.buildRequest(updateRequest, defaultAccessToken),
                                                                                  ProjectResponseDto.class,
                                                                                  id);

        Assertions.assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode().value());
        Assertions.assertNotNull(updateResponse.getBody());
        Assertions.assertNotNull(updateResponse.getBody().id());
        Assertions.assertEquals(updateRequest.name(), updateResponse.getBody().name());
        Assertions.assertNotEquals(createRequest.name(), updateResponse.getBody().name());
    }

    @Test
    void setStatus() {
        ProjectRequestDto createRequest = IntegrationTestDataBuilder.buildProjectRequestDto();
        ResponseEntity<ProjectResponseDto> createResponse = restTemplate.postForEntity(BASE_PROJECT_URL,
                                                                                       IntegrationTestDataBuilder.buildRequest(createRequest, defaultAccessToken),
                                                                                       ProjectResponseDto.class);
        UUID id = createResponse.getBody().id();
        ProjectStatus status = ProjectStatus.DONE;

        ProjectRequestDto updateRequest = IntegrationTestDataBuilder.buildProjectRequestDto();
        ResponseEntity<ProjectResponseDto> updateResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{id}/status/{status}",
                                                                                  HttpMethod.PUT,
                                                                                  IntegrationTestDataBuilder.buildRequest(updateRequest, defaultAccessToken),
                                                                                  ProjectResponseDto.class,
                                                                                  id,
                                                                                  status.name().toLowerCase(Locale.getDefault()));

        Assertions.assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode().value());
        Assertions.assertNull(updateResponse.getBody());

        ResponseEntity<ProjectResponseDto> getResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{id}",
                                                                               HttpMethod.GET,
                                                                               IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                               ProjectResponseDto.class,
                                                                               id);

        Assertions.assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode().value());
        ProjectResponseDto getResponseBody = getResponse.getBody();
        Assertions.assertNotNull(getResponseBody);
        Assertions.assertEquals(id, getResponseBody.id());
        Assertions.assertEquals(status, getResponseBody.status());
    }

    @Test
    void updateWithExistingName() {
        ProjectRequestDto request1 = IntegrationTestDataBuilder.buildProjectRequestDto();
        restTemplate.postForEntity(BASE_PROJECT_URL,
                                   IntegrationTestDataBuilder.buildRequest(request1, defaultAccessToken),
                                   ProjectResponseDto.class);
        ProjectRequestDto request2 = IntegrationTestDataBuilder.buildProjectRequestDto();
        ResponseEntity<ProjectResponseDto> response2 = restTemplate.postForEntity(BASE_PROJECT_URL,
                                                                                  IntegrationTestDataBuilder.buildRequest(request2, defaultAccessToken),
                                                                                  ProjectResponseDto.class);

        ResponseEntity<ErrorsDto> errorResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{id}",
                                                                        HttpMethod.PUT,
                                                                        IntegrationTestDataBuilder.buildRequest(request1, defaultAccessToken),
                                                                        ErrorsDto.class,
                                                                        response2.getBody().id());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertTrue(errors.getFirst().message().contains("already exists"));
    }

    @Test
    void delete() {
        ProjectRequestDto request = IntegrationTestDataBuilder.buildProjectRequestDto();
        UUID id = restTemplate.postForEntity(BASE_PROJECT_URL,
                                             IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                             ProjectResponseDto.class).getBody().id();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{id}",
                                                                    HttpMethod.DELETE,
                                                                    IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                    Void.class,
                                                                    id);

        Assertions.assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode().value());

        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_PROJECT_URL + "/{id}",
                                                                   HttpMethod.DELETE,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   id);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    void deleteNotFound() {
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_PROJECT_URL + "/{id}",
                                                                   HttpMethod.DELETE,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
    }
}
