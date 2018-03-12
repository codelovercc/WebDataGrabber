package com.cool.exception;

/**
 * Created by codelover on 17/4/8.
 */
public class ProjectRunTimeException extends RuntimeException {

    public ProjectRunTimeException() {
    }

    public ProjectRunTimeException(String message) {
        super(message);
    }

    public ProjectRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectRunTimeException(Throwable cause) {
        super(cause);
    }

    public ProjectRunTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
