package com.cool.exception;


/**
 * Created by codelover on 18/3/7.
 */
public class ProjectIOException extends ProjectException {
    public ProjectIOException() {
    }

    public ProjectIOException(String message) {
        super(message);
    }

    public ProjectIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectIOException(Throwable cause) {
        super(cause);
    }

    public ProjectIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
