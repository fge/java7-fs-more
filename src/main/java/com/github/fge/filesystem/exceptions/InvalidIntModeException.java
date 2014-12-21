package com.github.fge.filesystem.exceptions;

public final class InvalidIntModeException
    extends IllegalArgumentException
{
    public InvalidIntModeException()
    {
    }

    public InvalidIntModeException(final String s)
    {
        super(s);
    }

    public InvalidIntModeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public InvalidIntModeException(final Throwable cause)
    {
        super(cause);
    }
}
