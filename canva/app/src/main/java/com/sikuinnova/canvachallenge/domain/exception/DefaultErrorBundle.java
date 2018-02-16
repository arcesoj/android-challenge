package com.sikuinnova.canvachallenge.domain.exception;

/**
 * Created by josearce on 7/2/17.
 */

public class DefaultErrorBundle implements ErrorBundle {

    private final Exception exception;

    public DefaultErrorBundle(Exception exception) {
        this.exception = exception;
    }

    @Override
    public Exception getException() {
        return exception;
    }

}