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
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${todo.login.max-attempts}")
    private int maxAttempts;

    @Test
    void login() {
        LoginRequestDto loginRequestDto = IntegrationTestDataBuilder.buildDefaultLoginRequest();
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
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals("Invalid username or password", errors.getFirst().message());
    }

    @Test
    void loginUnauthorizedUserLocked() {
        LoginRequestDto request = IntegrationTestDataBuilder.buildLoginRequest(IntegrationTestDataBuilder.TEST_USERNAME,
                                                                               UUID.randomUUID().toString());
        ResponseEntity<ErrorsDto> errorResponse;

        for (int i = 0; i < maxAttempts; i++) {
            errorResponse = restTemplate.postForEntity(BASE_URL, request, ErrorsDto.class);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatusCode().value());
            Assertions.assertNotNull(errorResponse.getBody());
            List<ErrorDto> errors = errorResponse.getBody().errors();
            Assertions.assertFalse(CollectionUtils.isEmpty(errors));
            Assertions.assertEquals(1, errors.size());
            Assertions.assertEquals("Invalid username or password", errors.getFirst().message());
        }

        errorResponse = restTemplate.postForEntity(BASE_URL, request, ErrorsDto.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals("User is locked", errors.getFirst().message());

        Optional<AppUser> optionalUser = appUserRepository.findByUsername(IntegrationTestDataBuilder.TEST_USERNAME);
        Assertions.assertTrue(optionalUser.isPresent());
        AppUser user = optionalUser.get();
        Assertions.assertTrue(user.isLocked());
        Assertions.assertNotNull(user.lockedAt());
        Assertions.assertNotNull(user.loginAttempts());
        Assertions.assertNotNull(user.lastLoginAttemptAt());
    }

    @Test
    void loginWindowAttemptsNullifier() {
        LoginRequestDto invalidRequest = IntegrationTestDataBuilder.buildLoginRequest(IntegrationTestDataBuilder.TEST_USERNAME,
                                                                                      UUID.randomUUID().toString());
        ResponseEntity<ErrorsDto> errorResponse;

        for (int i = 0; i < maxAttempts - 1; i++) {
            errorResponse = restTemplate.postForEntity(BASE_URL, invalidRequest, ErrorsDto.class);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatusCode().value());
            Assertions.assertNotNull(errorResponse.getBody());
            List<ErrorDto> errors = errorResponse.getBody().errors();
            Assertions.assertFalse(CollectionUtils.isEmpty(errors));
            Assertions.assertEquals(1, errors.size());
            Assertions.assertEquals("Invalid username or password", errors.getFirst().message());
        }

        LoginRequestDto validRequest = IntegrationTestDataBuilder.buildDefaultLoginRequest();
        ResponseEntity<LoginResponseDto> validResponse = restTemplate.postForEntity(BASE_URL, validRequest, LoginResponseDto.class);
        Assertions.assertEquals(HttpStatus.OK.value(), validResponse.getStatusCode().value());
        Assertions.assertNotNull(validResponse.getBody());

        Optional<AppUser> optionalUser = appUserRepository.findByIdAndTenantId(defaultUserId, defaultTenantId);
        Assertions.assertTrue(optionalUser.isPresent());
        AppUser user = optionalUser.get();
        Assertions.assertFalse(user.isLocked());
        Assertions.assertNull(user.lockedAt());
        Assertions.assertNull(user.loginAttempts());
        Assertions.assertNull(user.lastLoginAttemptAt());
    }

    @Test
    void loginWindowAttemptsOutsideWindow() {
        String username = IntegrationTestDataBuilder.TEST_USERNAME;
        LoginRequestDto request = IntegrationTestDataBuilder.buildLoginRequest(username, UUID.randomUUID().toString());
        ResponseEntity<ErrorsDto> errorResponse;

        for (int i = 0; i < maxAttempts - 1; i++) {
            errorResponse = restTemplate.postForEntity(BASE_URL, request, ErrorsDto.class);
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatusCode().value());
            Assertions.assertNotNull(errorResponse.getBody());
            List<ErrorDto> errors = errorResponse.getBody().errors();
            Assertions.assertFalse(CollectionUtils.isEmpty(errors));
            Assertions.assertEquals(1, errors.size());
            Assertions.assertEquals("Invalid username or password", errors.getFirst().message());
        }

        jdbcTemplate.update("update app_user set last_login_attempt_at = now() - interval '40 minutes' where username = ?", username);

        errorResponse = restTemplate.postForEntity(BASE_URL, request, ErrorsDto.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getStatusCode().value());
        Assertions.assertNotNull(errorResponse.getBody());
        List<ErrorDto> errors = errorResponse.getBody().errors();
        Assertions.assertFalse(CollectionUtils.isEmpty(errors));
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals("Invalid username or password", errors.getFirst().message());

        Optional<AppUser> optionalUser = appUserRepository.findByUsername(username);
        Assertions.assertTrue(optionalUser.isPresent());
        AppUser user = optionalUser.get();
        Assertions.assertFalse(user.isLocked());
        Assertions.assertNull(user.lockedAt());
        Assertions.assertEquals(1, user.loginAttempts());
        Assertions.assertNotNull(user.lastLoginAttemptAt());
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
