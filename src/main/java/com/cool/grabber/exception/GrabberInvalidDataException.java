package com.cool.grabber.exception;

/**
 * Created by codelover on 18/3/7.
 */
public class GrabberInvalidDataException extends GrabberException {
    public GrabberInvalidDataException() {
    }

    public GrabberInvalidDataException(String message) {
        super(message);
    }

    public GrabberInvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public GrabberInvalidDataException(Throwable cause) {
        super(cause);
    }

    public GrabberInvalidDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
