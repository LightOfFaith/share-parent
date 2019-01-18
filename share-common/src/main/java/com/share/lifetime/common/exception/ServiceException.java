package com.share.lifetime.common.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends AbstractHttpException {

    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
        setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
