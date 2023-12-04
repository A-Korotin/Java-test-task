package com.em.testtask.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {
    public BadRequestException(String message, Object... format) {
        super(HttpStatus.BAD_REQUEST, message, format);
    }
}
