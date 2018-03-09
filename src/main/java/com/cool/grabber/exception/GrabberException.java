package com.cool.grabber.exception;

/**
 * Created by codelover on 18/3/7.
 */
public class GrabberException extends Exception {
    public GrabberException() {
    }

    public GrabberException(String message) {
        super(message);
    }

    public GrabberException(String message, Throwable cause) {
        super(message, cause);
    }

    public GrabberException(Throwable cause) {
        super(cause);
    }

    public GrabberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
