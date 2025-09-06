package com.kush.todo.config;

import org.slf4j.MDC;

import java.util.Map;

import org.springframework.core.task.TaskDecorator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * The main purpose is to populate thread context for the {@code @Async} processing.
 * Memory leak is prevented as part of the finally-block context cleanup
 */
public class ContextAwareTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                MDC.clear();
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }
}