package com.kush.todo.controller;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.TaskStatus;
import com.kush.todo.dto.request.ProjectRequestDto;
import com.kush.todo.dto.request.TaskRequestDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.dto.response.ProjectResponseDto;
import com.kush.todo.dto.response.TaskResponseDto;
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

class TaskControllerIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_PROJECT_URL = "/api/projects";

    @Test
    void createTask() {
        UUID projectId = getProjectId();
        TaskRequestDto request = IntegrationTestDataBuilder.buildTaskRequestDto();
        ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                                                              IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                              TaskResponseDto.class,
                                                                              projectId);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        assertTask(response.getBody(), request, projectId);
    }

    @Test
    void createTaskWithUser() {
        UUID projectId = getProjectId();
        TaskRequestDto request = IntegrationTestDataBuilder.buildTaskRequestDto(defaultUserId);
        ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                                                              IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                              TaskResponseDto.class,
                                                                              projectId);

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        assertTask(response.getBody(), request, projectId);
    }

    private void assertTask(TaskResponseDto response, TaskRequestDto request, UUID projectId) {
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        Assertions.assertEquals(defaultTenantId, response.tenantId());
        Assertions.assertEquals(projectId, response.projectId());
        Assertions.assertEquals(request.name(), response.name());
        Assertions.assertEquals(request.description(), response.description());
        Assertions.assertEquals(request.assignedUserId(), response.assignedUserId());
        Assertions.assertNotNull(response.status());
        Assertions.assertNotNull(response.createdAt());
        Assertions.assertNotNull(response.updatedAt());
    }

    @Test
    void createTaskWithInvalidTitle() {
        UUID projectId = getProjectId();
        TaskRequestDto request = IntegrationTestDataBuilder.buildTaskRequestDto("");
        ResponseEntity<ErrorsDto> response = restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                                                        IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                                        ErrorsDto.class,
                                                                        projectId);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void getTask() {
        UUID projectId = getProjectId();
        TaskRequestDto request = IntegrationTestDataBuilder.buildTaskRequestDto();
        TaskResponseDto created = restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                                             IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                             TaskResponseDto.class,
                                                             projectId).getBody();

        ResponseEntity<TaskResponseDto> getResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{taskId}",
                                                                            HttpMethod.GET,
                                                                            IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                            TaskResponseDto.class,
                                                                            projectId,
                                                                            created.id());

        Assertions.assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode().value());
        assertTask(getResponse.getBody(), request, projectId);
    }

    @Test
    void getTaskNotFound() {
        UUID projectId = getProjectId();
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{absentId}",
                                                                   HttpMethod.GET,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   projectId,
                                                                   absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
    }

    @Test
    void getAllTasks() {
        UUID projectId = getProjectId();
        restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                   IntegrationTestDataBuilder.buildRequest(IntegrationTestDataBuilder.buildTaskRequestDto(), defaultAccessToken),
                                   TaskResponseDto.class,
                                   projectId);

        ParameterizedTypeReference<CustomPage<TaskResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<TaskResponseDto>> getAllResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks?page=1&size=10",
                                                                                           HttpMethod.GET,
                                                                                           IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                           responseType,
                                                                                           projectId);

        Assertions.assertEquals(HttpStatus.OK.value(), getAllResponse.getStatusCode().value());
        CustomPage<TaskResponseDto> body = getAllResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertFalse(CollectionUtils.isEmpty(body.items()));
    }

    @Test
    void searchTasks() {
        UUID projectId = getProjectId();
        String key = "UniquE-TasK-ParT";
        restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                   IntegrationTestDataBuilder.buildRequest(IntegrationTestDataBuilder.buildTaskRequestDto(key.toLowerCase(Locale.getDefault()) + UUID.randomUUID()), defaultAccessToken),
                                   TaskResponseDto.class,
                                   projectId);
        restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                   IntegrationTestDataBuilder.buildRequest(IntegrationTestDataBuilder.buildTaskRequestDto(key.toUpperCase(Locale.getDefault()) + UUID.randomUUID()), defaultAccessToken),
                                   TaskResponseDto.class,
                                   projectId);

        ParameterizedTypeReference<CustomPage<TaskResponseDto>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<CustomPage<TaskResponseDto>> searchResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/search?page=1&size=10&key={key}",
                                                                                           HttpMethod.GET,
                                                                                           IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                           responseType,
                                                                                           projectId,
                                                                                           key);

        Assertions.assertEquals(HttpStatus.OK.value(), searchResponse.getStatusCode().value());
        CustomPage<TaskResponseDto> body = searchResponse.getBody();
        Assertions.assertNotNull(body);
        List<TaskResponseDto> items = body.items();
        Assertions.assertFalse(CollectionUtils.isEmpty(items));
        items.forEach(item -> Assertions.assertTrue(
                item.name().toLowerCase(Locale.getDefault()).contains(key.toLowerCase(Locale.getDefault()))));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "1", "111111111111111111111111111111111111"})
    void searchTasksWithInvalidKey(String key) {
        UUID projectId = getProjectId();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(
                BASE_PROJECT_URL + "/{projectId}/tasks/search?page=1&size=10&key={key}",
                HttpMethod.GET,
                IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                ErrorsDto.class,
                projectId,
                key);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @ParameterizedTest
    @MethodSource("getAllWithInvalidPageArgs")
    void getAllTasksWithInvalidPage(int page, int size, String message) {
        UUID projectId = getProjectId();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(
                BASE_PROJECT_URL + "/{projectId}/tasks?page={page}&size={size}",
                HttpMethod.GET,
                IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                ErrorsDto.class,
                projectId,
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
    void updateTask() {
        UUID projectId = getProjectId();
        TaskRequestDto createRequest = IntegrationTestDataBuilder.buildTaskRequestDto();
        UUID updateId = restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                                   IntegrationTestDataBuilder.buildRequest(createRequest, defaultAccessToken),
                                                   TaskResponseDto.class,
                                                   projectId)
                                    .getBody()
                                    .id();

        TaskRequestDto updateRequest = IntegrationTestDataBuilder.buildTaskRequestDto();
        ResponseEntity<TaskResponseDto> updateResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{updateId}",
                                                                               HttpMethod.PUT,
                                                                               IntegrationTestDataBuilder.buildRequest(updateRequest, defaultAccessToken),
                                                                               TaskResponseDto.class,
                                                                               projectId,
                                                                               updateId);

        Assertions.assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode().value());
        Assertions.assertNotNull(updateResponse.getBody());
        Assertions.assertNotNull(updateResponse.getBody().id());
        Assertions.assertEquals(updateRequest.name(), updateResponse.getBody().name());
        Assertions.assertNotEquals(createRequest.name(), updateResponse.getBody().name());
    }

    @Test
    void setTaskStatus() {
        UUID projectId = getProjectId();
        TaskRequestDto createRequest = IntegrationTestDataBuilder.buildTaskRequestDto();
        UUID updateId = restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                                   IntegrationTestDataBuilder.buildRequest(createRequest, defaultAccessToken),
                                                   TaskResponseDto.class,
                                                   projectId)
                                    .getBody()
                                    .id();

        TaskStatus status = TaskStatus.DONE;
        TaskRequestDto updateRequest = IntegrationTestDataBuilder.buildTaskRequestDto();
        ResponseEntity<TaskResponseDto> updateResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{updateId}/status/{status}",
                                                                               HttpMethod.PUT,
                                                                               IntegrationTestDataBuilder.buildRequest(updateRequest, defaultAccessToken),
                                                                               TaskResponseDto.class,
                                                                               projectId,
                                                                               updateId,
                                                                               status.name().toLowerCase(Locale.getDefault()));

        Assertions.assertEquals(HttpStatus.OK.value(), updateResponse.getStatusCode().value());
        Assertions.assertNull(updateResponse.getBody());

        ResponseEntity<TaskResponseDto> getResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{updateId}",
                                                                            HttpMethod.GET,
                                                                            IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                            TaskResponseDto.class,
                                                                            projectId,
                                                                            updateId);

        Assertions.assertEquals(HttpStatus.OK.value(), getResponse.getStatusCode().value());
        TaskResponseDto getResponseBody = getResponse.getBody();
        Assertions.assertNotNull(getResponseBody);
        Assertions.assertEquals(updateId, getResponseBody.id());
        Assertions.assertEquals(status, getResponseBody.status());
    }

    @Test
    void deleteTask() {
        UUID projectId = getProjectId();
        TaskRequestDto request = IntegrationTestDataBuilder.buildTaskRequestDto();
        UUID taskId = restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                                 IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                 TaskResponseDto.class,
                                                 projectId)
                                  .getBody()
                                  .id();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{taskId}",
                                                                    HttpMethod.DELETE,
                                                                    IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                    Void.class,
                                                                    projectId,
                                                                    taskId);

        Assertions.assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode().value());

        ResponseEntity<ErrorsDto> response = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{taskId}",
                                                                   HttpMethod.DELETE,
                                                                   IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                   ErrorsDto.class,
                                                                   projectId,
                                                                   taskId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
    }

    @Test
    void deleteProjectWillDeleteTask() {
        UUID projectId = getProjectId();
        TaskRequestDto request = IntegrationTestDataBuilder.buildTaskRequestDto();
        UUID taskId = restTemplate.postForEntity(BASE_PROJECT_URL + "/{projectId}/tasks",
                                                 IntegrationTestDataBuilder.buildRequest(request, defaultAccessToken),
                                                 TaskResponseDto.class,
                                                 projectId)
                                  .getBody()
                                  .id();

        ResponseEntity<TaskResponseDto> successResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{taskId}",
                                                                                HttpMethod.GET,
                                                                                IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                                TaskResponseDto.class,
                                                                                projectId,
                                                                                taskId);
        Assertions.assertEquals(HttpStatus.OK.value(), successResponse.getStatusCode().value());

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}",
                                                                    HttpMethod.DELETE,
                                                                    IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                    Void.class,
                                                                    projectId,
                                                                    taskId);

        Assertions.assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCode().value());

        ResponseEntity<ErrorsDto> errorResponse = restTemplate.exchange(BASE_PROJECT_URL + "/{projectId}/tasks/{taskId}",
                                                                        HttpMethod.DELETE,
                                                                        IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                                                                        ErrorsDto.class,
                                                                        projectId,
                                                                        taskId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatusCode().value());
    }

    @Test
    void deleteTaskNotFound() {
        UUID projectId = getProjectId();
        String absentId = UUID.randomUUID().toString();
        ResponseEntity<ErrorsDto> response = restTemplate.exchange(
                BASE_PROJECT_URL + "/{projectId}/tasks/{absentId}",
                HttpMethod.DELETE,
                IntegrationTestDataBuilder.buildRequest(defaultAccessToken),
                ErrorsDto.class,
                projectId,
                absentId);

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        List<ErrorDto> errors = response.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
    }

    private UUID getProjectId() {
        ProjectRequestDto projectRequest = IntegrationTestDataBuilder.buildProjectRequestDto();
        ResponseEntity<ProjectResponseDto> response = restTemplate.postForEntity(BASE_PROJECT_URL,
                                                                                 IntegrationTestDataBuilder.buildRequest(projectRequest, defaultAccessToken), ProjectResponseDto.class);
        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());

        return response.getBody().id();
    }
}
