package com.cool.grabber.exception;

/**
 * Created by codelover on 18/3/7.
 */
public class GrabberRumTimeException extends RuntimeException {
    public GrabberRumTimeException() {
    }

    public GrabberRumTimeException(String message) {
        super(message);
    }

    public GrabberRumTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GrabberRumTimeException(Throwable cause) {
        super(cause);
    }

    public GrabberRumTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
