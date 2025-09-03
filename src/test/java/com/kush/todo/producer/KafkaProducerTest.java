package com.kush.todo.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.todo.BaseTest;
import com.kush.todo.validator.KafkaEventValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.kafka.core.KafkaTemplate;

class KafkaProducerTest extends BaseTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private KafkaEventValidator eventValidator;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Test
    void send() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(JsonProcessingException.class);

        IllegalArgumentException actual = Assertions.assertThrows(IllegalArgumentException.class, () -> kafkaProducer.send("key", "value"));
        Assertions.assertEquals("Cannot serialise event", actual.getMessage());
    }
}