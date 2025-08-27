package com.kush.todo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record LoginResponseDto(
        @JsonProperty("access_token")
        String accessToken
) {
}