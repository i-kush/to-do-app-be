package com.kush.todo;

import jakarta.annotation.PreDestroy;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration-test")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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
}
