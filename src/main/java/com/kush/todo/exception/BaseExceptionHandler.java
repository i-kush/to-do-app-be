package com.kush.todo.exception;

import com.kush.todo.dto.response.ErrorDto;
import com.kush.todo.dto.response.ErrorsDto;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsDto> handle(MethodArgumentNotValidException e) {
        List<ErrorDto> errors = e.getBindingResult()
                                 .getFieldErrors()
                                 .stream()
                                 .map(er -> new ErrorDto(String.format("%s -> %s", er.getField(), er.getDefaultMessage())))
                                 .toList();
        log.warn("Validation failed: {} ", errors, e);

        return new ResponseEntity<>(new ErrorsDto(errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * For some reason in Spring 6.1+ {@link HandlerMethodValidationException} is thrown instead of {@link MethodArgumentNotValidException}.
     * Moreover, {@link MethodValidationResult#getAllValidationResults} is deprecated and does not have adequate alternative...waiting for a new Spring version
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorsDto> handle(HandlerMethodValidationException e) {
        List<ErrorDto> errors = new ArrayList<>();
        for (Object rawError : e.getDetailMessageArguments()) {
            String[] rawErrorParts = rawError.toString().split(",");
            for (int i = 0; i < rawErrorParts.length; i++) {
                String errorPart = i == 0 ? rawErrorParts[i] : rawErrorParts[i].substring(5);
                errors.add(new ErrorDto(errorPart.trim()));
            }
        }
        log.warn("Validation failed: {} ", errors, e);

        return new ResponseEntity<>(new ErrorsDto(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorsDto> handle(NotFoundException e) {
        log.warn("Not found", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(e.getMessage())), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorsDto> handle(NoResourceFoundException e) {
        log.warn("No static resource found", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(e.getMessage())), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorsDto> handle(IllegalArgumentException e) {
        log.warn("Invalid argument", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(e.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorsDto> handle(MethodArgumentTypeMismatchException e) {
        log.warn("Invalid argument type", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(String.format("Invalid type for '%s'", e.getName()))), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorsDto> handle(MissingServletRequestParameterException e) {
        log.warn("Missing request param", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(String.format("Missing request param '%s'", e.getParameterName()))), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorsDto> handle(HttpMediaTypeNotSupportedException e) {
        log.warn("Invalid content type", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(e.getMessage())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorsDto> handle(ConstraintViolationException e) {
        List<ErrorDto> errors = e.getConstraintViolations()
                                 .stream()
                                 .map(c -> new ErrorDto(String.format("%s %s", c.getPropertyPath(), c.getMessage())))
                                 .toList();
        log.warn("Constraint violation", e);
        return new ResponseEntity<>(new ErrorsDto(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorsDto> handle(HttpMessageNotReadableException e) {
        String[] messageParts = e.getMessage().split(":");
        String message = messageParts.length > 1 ? messageParts[0] : e.getMessage();
        log.error("Invalid HTTP request", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(message)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorsDto> handle(UnauthorizedException e) {
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(e.getMessage())), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorsDto> handle(AuthorizationDeniedException e) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<ErrorsDto> handle(DbActionExecutionException e) {
        Throwable cause = e.getCause();
        String message = "Unknown data error";
        if (cause instanceof DuplicateKeyException duplicateKeyException) {
            message = duplicateKeyException.getMessage().split("Detail: Key ")[1];
        }
        log.error("Database action error error", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto(message)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorsDto> handle(Exception e) {
        log.error("Unknown error", e);
        return new ResponseEntity<>(new ErrorsDto(new ErrorDto("Unknown application error")), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
