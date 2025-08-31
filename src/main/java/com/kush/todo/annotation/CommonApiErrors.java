package com.kush.todo.annotation;

import com.kush.todo.dto.response.ErrorsDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "400", description = "Invalid input data",
             content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
@ApiResponse(responseCode = "401", description = "User is not authorized",
             content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
@ApiResponse(responseCode = "403", description = "Operation is forbidden",
             content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
@ApiResponse(responseCode = "500", description = "Internal Server Error",
             content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
public @interface CommonApiErrors {
}
