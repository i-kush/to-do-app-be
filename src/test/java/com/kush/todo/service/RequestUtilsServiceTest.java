package com.kush.todo.service;

import com.kush.todo.config.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class RequestUtilsServiceTest {

    private final RequestUtilsService requestUtilsService = new RequestUtilsService();

    @ParameterizedTest
    @MethodSource("getIsAllowedEndpointParams")
    void isAllowedEndpoint(String endpoint, boolean expected) {
        boolean isAllowed = Assertions.assertDoesNotThrow(() -> requestUtilsService.isAllowedEndpoint(endpoint));
        Assertions.assertEquals(expected, isAllowed);
    }

    public static Stream<Arguments> getIsAllowedEndpointParams() {
        return Stream.concat(
                Stream.of(
                        Arguments.of("/test", false),
                        Arguments.of("/api/auth/login/anton", false)
                ),
                SecurityConfig.ALLOWED_ENDPOINTS.stream().map(e -> Arguments.of(e, true))
        );
    }
}