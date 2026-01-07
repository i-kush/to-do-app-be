package com.kush.todo.producer;

import com.kush.todo.validator.KafkaEventValidator;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaEventValidator kafkaEventValidator;

    public void send(Object event, String topic) {
        kafkaEventValidator.validate(event, topic);
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
    }
}
