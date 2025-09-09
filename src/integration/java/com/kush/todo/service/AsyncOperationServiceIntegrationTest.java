package com.kush.todo.service;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.constant.CommonErrorMessages;
import com.kush.todo.dto.async.AsyncOperationDto;
import com.kush.todo.dto.async.AsyncOperationEventDto;
import com.kush.todo.dto.async.AsyncOperationStatus;
import com.kush.todo.dto.response.AsyncOperationQueuedResponseDto;
import com.kush.todo.entity.AppUser;
import com.kush.todo.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

class AsyncOperationServiceIntegrationTest extends BaseIntegrationTest {

    private static final String TEST_TOPIC_NAME = "test-topic";

    @Autowired
    private AsyncOperationService asyncOperationService;

    @Test
    void queueOperation() {
        AsyncOperationQueuedResponseDto queuedResponse = asyncOperationService.queueOperation(defaultTenantId,
                                                                                              new TestAsync(UUID.randomUUID().toString()),
                                                                                              TEST_TOPIC_NAME);

        Assertions.assertNotNull(queuedResponse);
        Assertions.assertNotNull(queuedResponse.id());

        AsyncOperationDto<AppUser> operation = asyncOperationService.getOperation(queuedResponse.id(), defaultTenantId);
        Assertions.assertNotNull(operation);
        Assertions.assertNotNull(operation.id());
        Assertions.assertNotNull(operation.tenantId());
        Assertions.assertEquals(AsyncOperationStatus.IN_PROGRESS, operation.status());
    }

    @Test
    void getOperationNotFound() {
        UUID operationId = UUID.randomUUID();
        String key = asyncOperationService.toKey(operationId, defaultTenantId);
        NotFoundException actual = Assertions.assertThrows(NotFoundException.class, () -> asyncOperationService.getOperation(operationId, defaultTenantId));
        Assertions.assertEquals(String.format(CommonErrorMessages.PATTERN_NOT_FOUND, key), actual.getMessage());
    }

    @Test
    void executeOperation() {
        TestAsync request = new TestAsync(UUID.randomUUID().toString());
        AsyncOperationQueuedResponseDto queuedResponse = asyncOperationService.queueOperation(defaultTenantId, request, TEST_TOPIC_NAME);

        Assertions.assertNotNull(queuedResponse);
        Assertions.assertNotNull(queuedResponse.id());
        AsyncOperationEventDto<Object> eventDto = AsyncOperationEventDto.builder()
                                                                        .operationId(queuedResponse.id())
                                                                        .tenantId(defaultTenantId)
                                                                        .request(request)
                                                                        .build();
        TestAsync response = new TestAsync(UUID.randomUUID().toString());
        asyncOperationService.executeOperation(eventDto, () -> response);

        AsyncOperationDto<TestAsync> operation = asyncOperationService.getOperation(queuedResponse.id(), defaultTenantId);
        Assertions.assertNotNull(operation);
        Assertions.assertNotNull(operation.id());
        Assertions.assertNotNull(operation.tenantId());
        Assertions.assertEquals(AsyncOperationStatus.SUCCESS, operation.status());
        Assertions.assertEquals(response, operation.result());
    }

    @Test
    void executeOperationFailure() {
        TestAsync request = new TestAsync(UUID.randomUUID().toString());
        AsyncOperationQueuedResponseDto queuedResponse = asyncOperationService.queueOperation(defaultTenantId, request, TEST_TOPIC_NAME);

        Assertions.assertNotNull(queuedResponse);
        Assertions.assertNotNull(queuedResponse.id());
        AsyncOperationEventDto<Object> eventDto = AsyncOperationEventDto.builder()
                                                                        .operationId(queuedResponse.id())
                                                                        .tenantId(defaultTenantId)
                                                                        .build();
        asyncOperationService.executeOperation(eventDto, () -> new TestAsync(UUID.randomUUID().toString()).get());

        AsyncOperationDto<TestAsync> operation = asyncOperationService.getOperation(queuedResponse.id(), defaultTenantId);
        Assertions.assertNotNull(operation);
        Assertions.assertNotNull(operation.id());
        Assertions.assertNotNull(operation.tenantId());
        Assertions.assertEquals(AsyncOperationStatus.ERROR, operation.status());
        Assertions.assertNull(operation.result());
    }

    record TestAsync(String value) {

        String get() {
            throw new UnsupportedOperationException();
        }
    }
}
