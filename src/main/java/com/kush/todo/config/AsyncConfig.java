package com.kush.todo.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    public static final String THREAD_POOL_PLATFORM_ASYNC = "asyncPlatformThreadPoolExecutor";
    public static final String THREAD_POOL_VIRTUAL_ASYNC = "asyncVirtualThreadPoolExecutor";

    @Bean(THREAD_POOL_PLATFORM_ASYNC)
    public Executor asyncPlatformExecutor(@Value("${todo.executor.async.core-size}") int corePoolSize,
                                          @Value("${todo.executor.async.max-size}") int maxPoolSize,
                                          @Value("${todo.executor.async.queue-capacity}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("async-platform-");
        executor.setTaskDecorator(new ContextAwareTaskDecorator());

        return executor;
    }

    @SuppressWarnings("PMD.CloseResource") //False positive - spring boot by default invokes destroy methods 'close' and 'shutdown' if available
    @Bean(THREAD_POOL_VIRTUAL_ASYNC)
    public Executor asyncVirtualExecutor() {
        ThreadFactory factory = Thread.ofVirtual()
                                      .name("async-virtual-")
                                      .factory();
        ExecutorService executor = Executors.newThreadPerTaskExecutor(factory);
        return task -> executor.execute(new ContextAwareTaskDecorator().decorate(task));
    }
}
