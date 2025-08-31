package com.kush.todo.controller;

import com.kush.todo.annotation.CommonApiErrors;
import com.kush.todo.dto.request.LoginRequestDto;
import com.kush.todo.dto.response.LoginResponseDto;
import com.kush.todo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User login", description = "Generates an access token for further communication with a system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login and access token generation"),
    })
    @CommonApiErrors
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }
}