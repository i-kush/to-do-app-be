package com.kush.todo.validator;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class KafkaEventValidator {

    public void validate(Object event, String topic) {
        Assert.notNull(event, "event cannot be null");
        Assert.notNull(topic, "topic cannot be null");
    }
}
