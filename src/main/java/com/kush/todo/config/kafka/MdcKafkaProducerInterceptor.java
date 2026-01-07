package com.kush.todo.config.kafka;

import com.kush.todo.constant.MdcConstants;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MdcKafkaProducerInterceptor implements ProducerInterceptor<Object, Object> {

    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        String userId = MDC.get(MdcConstants.USER_ID);
        if (userId != null) {
            record.headers().add(MdcConstants.USER_ID, userId.getBytes(StandardCharsets.UTF_8));
        }
        return record;
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
