package com.kush.todo;

import com.kush.todo.dto.request.LoginRequestDto;
import com.kush.todo.dto.response.LoginResponseDto;
import jakarta.annotation.PreDestroy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration-test")
@Sql(scripts = "/test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:17.0-alpine")
            .withDatabaseName("to-do-database")
            .withUsername("postgres")
            .withPassword("password2")
            .withExposedPorts(5432)
            .waitingFor(Wait.forListeningPort());

    static {
        DB.start();
    }

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected String defaultAccessToken;
    protected UUID defaultTenantId;
    protected UUID systemTenantId;

    @DynamicPropertySource
    public static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB::getJdbcUrl);
        registry.add("spring.datasource.username", DB::getUsername);
        registry.add("spring.datasource.password", DB::getPassword);
    }

    @PreDestroy
    public static void tearDown() {
        DB.stop();
    }

    @BeforeEach
    public void setUp() throws IOException {
        jdbcTemplate.execute(Files.readString(Paths.get("src/integration/resources/test-init.sql")));
        defaultAccessToken = login(IntegrationTestDataBuilder.buildDefaultLoginRequest());

        defaultTenantId = jdbcTemplate.queryForObject("select id from tenant where name = 'TestTenant' limit 1", UUID.class);
        systemTenantId = jdbcTemplate.queryForObject("select id from tenant where name = 'system' limit 1", UUID.class);
    }

    protected String login(LoginRequestDto loginRequestDto) {
        ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity("/api/auth/login", loginRequestDto, LoginResponseDto.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().accessToken());

        return response.getBody().accessToken();
    }
}
