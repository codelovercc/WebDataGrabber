package com.cool.exception;

/**
 * Created by codelover on 17/4/10.
 */
public class ProjectFormatException extends ProjectRunTimeException {

    public ProjectFormatException() {
        super();
    }

    public ProjectFormatException(String message) {
        super(message);
    }

    public ProjectFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectFormatException(Throwable cause) {
        super(cause);
    }
}
