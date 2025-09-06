package com.kush.todo.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    public static final String THREAD_POOL_ASYNC = "asyncThreadPoolExecutor";

    @Bean(THREAD_POOL_ASYNC)
    public Executor asyncAuditExecutor(@Value("${executor.async.core-size}") int corePoolSize,
                                       @Value("${executor.async.max-size}") int maxPoolSize,
                                       @Value("${executor.async.queue-capacity}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("async-audit-");
        executor.setTaskDecorator(new ContextAwareTaskDecorator());

        return executor;
    }

}
