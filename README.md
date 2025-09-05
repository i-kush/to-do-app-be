# ToDo Application

A multi-tenant ToDo application with the tenant data isolation, users, projects, tasks and other business entities.

As for the security stateless custom JWT based on username/password is used, where permissions are granulated per role and access
to endpoints is protected by the spring security from both authorisation and authentication prospectives.

The purpose of the application is mostly demo, since it's aggregating most of the commonly used technologies in the modern microservices.

## Requirements

- for simple local run `docker` and `docker-compose` should be installed
- for development Java 21
- Gradle is optional for development, since there is gradle wrapper

## Stack

| Tech                | Comment                                                                                                                     |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `Spring Boot `      |                                                                                                                             |
| `Spring Data JDBC`  | Plain spring data repositories are used mixed with the native queries                                                       |
| `Spring Security`   | JWT encoding/decoding is delegated to spring as well as common `401`/`403` handling                                         |
| `Spring Actuator`   | Running on a separate port to simplify business and infra endpoint routing on env - [link](localhost:8081/actuator)         |
| `Spring Web`        |                                                                                                                             |
| `Spring Validation` |                                                                                                                             |
| `Spring OAuth2`     | Resource server                                                                                                             |
| `Spring Kafka`      | Events are going through the Kafka as part of async processing, usually result is written to Redis                          |
| `Liquibase`         |                                                                                                                             |
| `PostgreSQL`        |                                                                                                                             |
| `Redis`             | Used for both async results temp storing and spring `@Cachable` serving with dynamic generic Jackson mapping for the values |
| `Micrometer`        | `spanId` and `traceId` are used for all communication patterns (both async and sync) and logging                            |
| `ArchUnit`          |                                                                                                                             |
| `Spring Data JDBC`  | Plain spring data repositories are used mixed with the native queries                                                       |
| `Swagger`           | [Link](http://localhost:8080/swagger-ui/index.html)                                                                         |
| `Logback`           | For non-local run JSON-logging is enabled, it will respects all MDC vars, including both custom ones and from libs          |

## Build

- run only unit tests: `./gradlew test`
- run only integration tests: `./gradlew integrationTest -x test`
- build without tests: `./gradlew build -x test`
- build with unit tests: `./gradlew build`
- build with both unit and integration tests: `./gradlew build -Dtest.profile=integration`
- build test coverage report (first `test` and `integrationTest` should be executed: `./gradlew jacocoTestReport`
- build image locally: `docker build -t to-do-app .`

## Local run

- run dockerized full BE with infra: `docker-compose --profile backend up`
- run dockerized full BE with infra rebuilding the BE image: `docker-compose --profile backend up --build`
- run local infra without BE itself, it's assumed BE will be run in dev mode with IDE: `docker-compose up`
- use `local` spring boot profile
- postman collection with predefined pre- and post-execution scripts could be found under root dir `postman`

## Important env variables

- `LOGGING_LEVEL_COM_KUSH_TODO=LOG_LEVEL`, where `LOG_LEVEL` is a desired log level for the custom logs, same could be applied to other
  packages
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` - actual kafka bootstrap server URL with port
- `SPRING_DATA_REDIS_HOST` - Redis host
- `SPRING_DATA_REDIS_PORT` - Redis host
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_SECRET_KEY` - secret key to use for the JWT signature 
- `SPRING_DATASOURCE_URL` - database JDBC URL with database name path variable 
- `SPRING_DATASOURCE_USERNAME` - database username 
- `SPRING_DATASOURCE_PASSWORD` - database password 