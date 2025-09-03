package com.kush.todo.validator;

import com.kush.todo.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class KafkaEventValidatorTest extends BaseTest {

    private final KafkaEventValidator validator = new KafkaEventValidator();

    @Test
    void validateSuccess() {
        Assertions.assertDoesNotThrow(() -> validator.validate(new Object(), "test"));
    }

    @Test
    void validateEventFailure() {
        IllegalArgumentException actual = Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate(null, "test"));
        Assertions.assertEquals("event cannot be null", actual.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void validateTopicFailure(String invaliTopic) {
        IllegalArgumentException actual = Assertions.assertThrows(IllegalArgumentException.class, () -> validator.validate("test", invaliTopic));
        Assertions.assertEquals("topic cannot be empty", actual.getMessage());
    }
}