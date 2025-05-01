package com.kush.todo.dto.response;

import java.util.Collections;
import java.util.List;

public record ErrorsDto(
        List<ErrorDto> errors
) {

    public ErrorsDto(ErrorDto error) {
        this(Collections.singletonList(error));
    }
}