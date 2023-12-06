package com.em.testtask.controller.advice;

import com.em.testtask.dto.error.ErrorDto;
import com.em.testtask.exception.BaseException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlingControllerAdvice {

    @ExceptionHandler(BaseException.class)
    public ErrorDto handleBase(BaseException e, HttpServletResponse response) {
        response.setStatus(e.getStatus().value());
        log.warn("{} occurred with cause: {}", e.getClass().getSimpleName(), e.getMessage());
        return ErrorDto.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleBadRequest(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(field, errorMessage);
        });
        log.warn("Invalid input - binding errors: {}", errors);

        return new ErrorDto("Binding error", errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorDto verboselyHandleMismatchException(MethodArgumentTypeMismatchException e) {
        String requiredType = Objects.nonNull(e.getRequiredType()) ? e.getRequiredType().getSimpleName() : "unknown";
        String argumentName = e.getName();
        Map<String, String> errors = new HashMap<>(2);
        errors.put("expectedType", requiredType);
        errors.put("argument", argumentName);
        log.warn("Invalid input - type mismatch: {}", errors);

        return new ErrorDto("Argument type mismatch", errors);
    }
}
