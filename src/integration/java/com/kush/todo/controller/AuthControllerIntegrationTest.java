package com.kush.todo.controller;

import com.kush.todo.BaseIntegrationTest;
import com.kush.todo.IntegrationTestDataBuilder;
import com.kush.todo.dto.request.LoginRequestDto;
import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.dto.response.LoginResponseDto;
import com.kush.todo.entity.AppUser;
import com.kush.todo.repository.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.util.CollectionUtils;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

    public static final String BASE_URL = "/api/auth/login";

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void login() {
        LoginRequestDto loginRequestDto = IntegrationTestDataBuilder.buildLoginRequest();
        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(BASE_URL, loginRequestDto, LoginResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());

        String accessToken = response.getBody().accessToken();
        Assertions.assertNotNull(accessToken);

        Jwt jwt = jwtDecoder.decode(accessToken);
        Assertions.assertNotNull(jwt);
        Assertions.assertNotNull(jwt.getIssuedAt());
        Assertions.assertNotNull(jwt.getExpiresAt());
        Assertions.assertFalse(CollectionUtils.isEmpty(jwt.getClaims()));
        Assertions.assertNotNull(jwt.getClaimAsString("scope")); //ToDo add scope check

        Optional<AppUser> foundUserOptional = appUserRepository.findByUsername(loginRequestDto.username());
        Assertions.assertTrue(foundUserOptional.isPresent());

        AppUser foundUser = foundUserOptional.get();
        Assertions.assertEquals(foundUser.id().toString(), jwt.getSubject());
        Assertions.assertEquals(foundUser.roleId().toString(), jwt.getClaimAsString("role"));
        Assertions.assertEquals(foundUser.tenantId().toString(), jwt.getClaimAsString("tenant"));
        Assertions.assertEquals(foundUser.username(), jwt.getClaimAsString("username"));
        Assertions.assertEquals(loginRequestDto.username(), jwt.getClaimAsString("username"));
        Assertions.assertEquals(foundUser.email(), jwt.getClaimAsString("email"));
    }

    @ParameterizedTest
    @MethodSource("getLoginUnauthorizedParams")
    void loginUnauthorized(LoginRequestDto loginRequestDto) {
        ResponseEntity<ErrorsDto> errorResponse = restTemplate.postForEntity(BASE_URL, loginRequestDto, ErrorsDto.class);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatusCode().value());
        Assertions.assertNull(errorResponse.getBody());
    }

    @ParameterizedTest
    @MethodSource("getLoginRequestDataValidationParams")
    void loginRequestDataValidation(LoginRequestDto loginRequestDto, String expectedError) {
        ResponseEntity<ErrorsDto> errorResponse = restTemplate.postForEntity(BASE_URL, loginRequestDto, ErrorsDto.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        boolean isExpectedErrorPresent = errors.stream()
                                               .map(ErrorDto::message)
                                               .anyMatch(expectedError::equals);
        Assertions.assertTrue(isExpectedErrorPresent);
    }

    private static Stream<Arguments> getLoginUnauthorizedParams() {
        return Stream.of(
                Arguments.of(LoginRequestDto.builder()
                                            .username(IntegrationTestDataBuilder.TEST_USERNAME)
                                            .password("invalid-password-value")
                                            .build()),
                Arguments.of(LoginRequestDto.builder()
                                            .username("invalid-username-value")
                                            .password(IntegrationTestDataBuilder.TEST_PASSWORD)
                                            .build()),
                Arguments.of(LoginRequestDto.builder()
                                            .username("both-invalid")
                                            .password("both-invalid")
                                            .build())
        );
    }

    private static Stream<Arguments> getLoginRequestDataValidationParams() {
        return Stream.of(
                Arguments.of(LoginRequestDto.builder()
                                            .username("")
                                            .password("valid")
                                            .build(), "username -> must not be blank"),
                Arguments.of(LoginRequestDto.builder()
                                            .username("")
                                            .password("valid")
                                            .build(), "username -> size must be between 1 and 50"),
                Arguments.of(LoginRequestDto.builder()
                                            .username(null)
                                            .password("valid")
                                            .build(), "username -> must not be blank"),
                Arguments.of(LoginRequestDto.builder()
                                            .username("asdfasdfadsfadsfasdfasdfasdfasfasasdasfdsafsafsadfasdf")
                                            .password("valid")
                                            .build(), "username -> size must be between 1 and 50"),
                Arguments.of(LoginRequestDto.builder()
                                            .username("valid")
                                            .password("")
                                            .build(), "password -> must not be blank"),
                Arguments.of(LoginRequestDto.builder()
                                            .username("valid")
                                            .password("")
                                            .build(), "password -> size must be between 1 and 50"),
                Arguments.of(LoginRequestDto.builder()
                                            .username("valid")
                                            .password(null)
                                            .build(), "password -> must not be blank"),
                Arguments.of(LoginRequestDto.builder()
                                            .username("valid")
                                            .password("asdfasdfadsfadsfasdfasdfasdfasfasasdasfdsafsafsadfasdf")
                                            .build(), "password -> size must be between 1 and 50")

        );
    }
}
