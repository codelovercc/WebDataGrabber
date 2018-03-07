package com.cool.exception;

/**
 * Created by codelover on 18/3/7.
 */
public class ProjectIORunTimeException extends ProjectRunTimeException {
    public ProjectIORunTimeException() {
    }

    public ProjectIORunTimeException(String message) {
        super(message);
    }

    public ProjectIORunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectIORunTimeException(Throwable cause) {
        super(cause);
    }
}
