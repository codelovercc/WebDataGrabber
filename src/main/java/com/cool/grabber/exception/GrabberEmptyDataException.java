package com.cool.grabber.exception;

/**
 * Created by codelover on 18/3/7.
 */
public class GrabberEmptyDataException extends GrabberException {
    public GrabberEmptyDataException() {
    }

    public GrabberEmptyDataException(String message) {
        super(message);
    }

    public GrabberEmptyDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public GrabberEmptyDataException(Throwable cause) {
        super(cause);
    }

    public GrabberEmptyDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
