package com.github.fge.filesystem.exceptions;


public class InvalidModeInstructionException
        extends IllegalArgumentException
{
    
    public InvalidModeInstructionException() {
        super();
    }

    public InvalidModeInstructionException(String s) {
        super(s);
    }

    public InvalidModeInstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidModeInstructionException(Throwable cause) {
        super(cause);
    }
}