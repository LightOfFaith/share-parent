package com.share.lifetime.common.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractHttpException extends AbstractException {

    private static final long serialVersionUID = 1L;

    protected HttpStatus httpStatus;

    public AbstractHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractHttpException(String message) {
        super(message);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

}
