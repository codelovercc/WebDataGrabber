package com.cool.exception;

/**
 * Created by codelover on 17/3/23.
 */
public abstract class ProjectException extends Exception {

    public ProjectException() {
        super();
    }

    public ProjectException(String message) {
        super(message);
    }

    public ProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectException(Throwable cause) {
        super(cause);
    }
}
