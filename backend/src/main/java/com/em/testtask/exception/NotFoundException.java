package com.em.testtask.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException{
    public NotFoundException(String message, Object... format) {
        super(HttpStatus.NOT_FOUND, message, format);
    }
}
