package com.kush.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDto(
        @JsonProperty("access_token")
        String accessToken
) {
}