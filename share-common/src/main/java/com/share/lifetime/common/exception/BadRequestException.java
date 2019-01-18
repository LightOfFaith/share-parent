package com.share.lifetime.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractHttpException {

    private static final long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(message);
        setHttpStatus(HttpStatus.BAD_REQUEST);
    }

}
