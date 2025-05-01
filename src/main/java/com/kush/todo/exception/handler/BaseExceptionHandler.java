package com.kush.todo.exception.handler;

import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import com.kush.todo.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BaseExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsDto> handle(MethodArgumentNotValidException e) {
        List<ErrorDto> errors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDto(String.format("%s -> %s", error.getField(), error.getDefaultMessage())))
                .toList();
        log.error("Validation failed: {} ", errors, e);

        return new ResponseEntity<>(new ErrorsDto(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorsDto> handle(NotFoundException exception) {
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(exception.getMessage())), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorsDto> handle(Exception e) {
        log.error("Unknown error", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
