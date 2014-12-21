package com.github.fge.filesystem.exceptions;

import java.io.IOException;

public final class RecursiveOperationException
    extends IOException
{
    public RecursiveOperationException()
    {
    }

    public RecursiveOperationException(final String message)
    {
        super(message);
    }

    public RecursiveOperationException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    public RecursiveOperationException(final Throwable cause)
    {
        super(cause);
    }
}
