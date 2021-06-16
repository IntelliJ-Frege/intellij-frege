package com.plugin.frege.gradle;

public class GradleFregeException extends Exception {
    public GradleFregeException() {
        super();
    }

    public GradleFregeException(String message) {
        super(message);
    }

    public GradleFregeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GradleFregeException(Throwable cause) {
        super(cause);
    }

    protected GradleFregeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
