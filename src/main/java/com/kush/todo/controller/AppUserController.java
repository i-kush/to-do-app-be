package com.kush.todo.controller;

import com.kush.todo.dto.request.AppUserRequestDto;
import com.kush.todo.dto.response.AppUserResponseDto;
import com.kush.todo.dto.response.CustomPage;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @Operation(summary = "Create user", description = "Creates a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created user"),
            @ApiResponse(responseCode = "500", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AppUserResponseDto create(@Valid @RequestBody AppUserRequestDto userDto) {
        return appUserService.create(userDto, UUID.fromString("8cd702dc-fb77-4854-8192-3cb8b92def41")); //ToDo use tenant ID from the context
    }

    @Operation(summary = "Get user by ID", description = "Gets user details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "500", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppUserResponseDto get(@NotNull @PathVariable UUID id) {
        return appUserService.findByIdRequired(id);
    }

    @Operation(summary = "Get users", description = "Gets paginated users list with details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated users"),
            @ApiResponse(responseCode = "500", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CustomPage<AppUserResponseDto> getAll(@Min(1) @RequestParam int page, @Min(1) @Max(200) @RequestParam int size) {
        return appUserService.findAll(page, size);
    }

    @Operation(summary = "Update user by ID", description = "Updates user details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "500", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @PutMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppUserResponseDto update(@NotNull @PathVariable UUID id, @Valid @RequestBody AppUserRequestDto userDto) {
        return appUserService.update(id, userDto);
    }

    @Operation(summary = "Delete user by ID", description = "Deletes user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "500", description = "Error response", content = @Content(schema = @Schema(implementation = ErrorsDto.class)))
    })
    @DeleteMapping("{id}")
    public void delete(@NotNull @PathVariable UUID id) {
        appUserService.delete(id);
    }
}
