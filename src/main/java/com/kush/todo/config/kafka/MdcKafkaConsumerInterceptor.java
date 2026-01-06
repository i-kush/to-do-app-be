package com.kush.todo.config.kafka;

import com.kush.todo.constant.MdcConstants;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;

import org.springframework.kafka.listener.RecordInterceptor;

class MdcKafkaConsumerInterceptor implements RecordInterceptor<Object, Object> {

    @Override
    public ConsumerRecord<Object, Object> intercept(ConsumerRecord<Object, Object> record,
                                                    Consumer<Object, Object> consumer) {
        populateMdc(record, MdcConstants.USER_ID);
        return record;
    }

    @Override
    public void afterRecord(ConsumerRecord<Object, Object> record, Consumer<Object, Object> consumer) {
        RecordInterceptor.super.afterRecord(record, consumer);
        clearMdc(MdcConstants.USER_ID);
    }

    private void clearMdc(String... headerKeys) {
        for (String headerKey : headerKeys) {
            MDC.remove(headerKey);
        }
    }

    private void populateMdc(ConsumerRecord<Object, Object> record, String... headerKeys) {
        for (String headerKey : headerKeys) {
            Header headerValue = record.headers().lastHeader(headerKey);
            if (headerValue != null) {
                MDC.put(headerKey, new String(headerValue.value(), StandardCharsets.UTF_8));
            }
        }
    }
}