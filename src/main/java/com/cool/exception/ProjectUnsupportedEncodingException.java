package com.cool.exception;

/**
 * Created by codelover on 18/3/7.
 */
public class ProjectUnsupportedEncodingException extends ProjectException {
    public ProjectUnsupportedEncodingException() {
    }

    public ProjectUnsupportedEncodingException(String message) {
        super(message);
    }

    public ProjectUnsupportedEncodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectUnsupportedEncodingException(Throwable cause) {
        super(cause);
    }

    public ProjectUnsupportedEncodingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
