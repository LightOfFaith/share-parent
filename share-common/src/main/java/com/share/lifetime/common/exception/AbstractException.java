package com.share.lifetime.common.exception;

public abstract class AbstractException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractException(String message) {
        super(message);
    }

}
