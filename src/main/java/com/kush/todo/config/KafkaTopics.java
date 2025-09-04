package com.kush.todo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.kafka.topics")
public record KafkaTopics(
        String onboardTenant,
        String offboardTenant
) {
}
