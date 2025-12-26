package com.kush.todo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    public static final String CACHE_NAME_ASYNC_OPERATIONS = "asyncOperations";
    public static final String CACHE_NAME_USERS = "users";
    public static final Set<String> DEFAULT_CACHE_NAMES = Set.of(CACHE_NAME_ASYNC_OPERATIONS);

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     @Value("${spring.data.redis.ttl}") int ttl) {
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                                                                                        .enableUnsafeDefaultTyping()
                                                                                        .build();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(ttl))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                                .cacheDefaults(defaultConfig)
                                .initialCacheNames(DEFAULT_CACHE_NAMES)
                                .build();
    }

    @Bean(CACHE_NAME_ASYNC_OPERATIONS)
    public Cache asyncOperationsCache(CacheManager cacheManager) {
        return cacheManager.getCache(CACHE_NAME_ASYNC_OPERATIONS);
    }
}