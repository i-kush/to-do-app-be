package com.kush.todo.config;

import com.kush.todo.dto.CustomHeaders;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.models.GroupedOpenApi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi customGroupedOpenApi() {
        return GroupedOpenApi.builder()
                             .group("to-do-app")
                             .displayName("To-Do App OpenAPI")
                             .addOperationCustomizer((operation, handlerMethod) -> {
                                 addGlobalRequestHeaders(operation);
                                 addGlobalResponseHeaders(operation);
                                 return operation;
                             })
                             .build();
    }

    private void addGlobalRequestHeaders(Operation operation) {
        operation.addParametersItem(new HeaderParameter().name(HttpHeaders.AUTHORIZATION)
                                                         .description("Authorization JWT data")
                                                         .schema(new StringSchema())
                                                         .required(true));
    }

    private void addGlobalResponseHeaders(Operation operation) {
        operation.getResponses().
                 forEach((code, response) -> response.addHeaderObject(CustomHeaders.REQUEST_ID,
                                                                      new Header().description("Operation request ID")
                                                                                  .schema(new StringSchema())));
    }
}
