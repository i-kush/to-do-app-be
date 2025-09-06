package com.kush.todo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kush.todo.converter.JsonNodeReadConverter;
import com.kush.todo.converter.JsonNodeWriteConverter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

@Configuration
public class JdbcConfig {

    @Bean
    public JdbcCustomConversions jdbcCustomConversions(ObjectMapper objectMapper) {
        return new JdbcCustomConversions(List.of(
                new JsonNodeReadConverter(objectMapper),
                new JsonNodeWriteConverter(objectMapper)
        ));
    }
}
