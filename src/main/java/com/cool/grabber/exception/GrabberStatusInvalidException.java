package com.cool.grabber.exception;

/**
 * Created by codelover on 18/3/7.
 */
public class GrabberStatusInvalidException extends GrabberException {
    public GrabberStatusInvalidException() {
    }

    public GrabberStatusInvalidException(String message) {
        super(message);
    }

    public GrabberStatusInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public GrabberStatusInvalidException(Throwable cause) {
        super(cause);
    }

    public GrabberStatusInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
