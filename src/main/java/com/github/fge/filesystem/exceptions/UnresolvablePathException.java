package com.github.fge.filesystem.exceptions;

public class UnresolvablePathException extends UnsupportedOperationException {


    public UnresolvablePathException() {
    }


    public UnresolvablePathException(String message) {
        super(message);
    }

    public UnresolvablePathException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnresolvablePathException(Throwable cause) {
        super(cause);
    }

}
