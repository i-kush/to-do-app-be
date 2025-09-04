package com.kush.todo.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.todo.dto.async.AsyncOperationEventDto;
import com.kush.todo.dto.request.CreateTenantRequestDto;
import com.kush.todo.facade.TenantFacade;
import com.kush.todo.service.AsyncOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final TenantFacade tenantFacade;
    private final AsyncOperationService asyncOperationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.onboard-tenant}")
    public void onboardTenant(Message<String> message) throws JsonProcessingException {
        AsyncOperationEventDto<CreateTenantRequestDto> operation = objectMapper.readValue(message.getPayload(), new TypeReference<>() {
        });
        asyncOperationService.executeOperation(operation, () -> tenantFacade.create(operation.request()));
    }
}
