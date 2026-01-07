package com.kush.todo.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.RecordInterceptor;

@Configuration
public class KafkaConfig {

    @Bean
    public RecordInterceptor<Object, Object> mdcKafkaConsumerInterceptor() {
        return new MdcKafkaConsumerInterceptor();
    }
}
