package com.github.fge.filesystem.exceptions;


public final class InvalidModeInstructionException
    extends IllegalArgumentException
{

    public InvalidModeInstructionException()
    {
    }

    public InvalidModeInstructionException(final String s)
    {
        super(s);
    }

    public InvalidModeInstructionException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    public InvalidModeInstructionException(final Throwable cause)
    {
        super(cause);
    }
}