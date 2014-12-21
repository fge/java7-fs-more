package com.github.fge.filesystem.exceptions;

import java.io.IOException;

public final class RecursiveCopyException
    extends IOException
{
    public RecursiveCopyException()
    {
    }

    public RecursiveCopyException(final String message)
    {
        super(message);
    }

    public RecursiveCopyException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public RecursiveCopyException(final Throwable cause)
    {
        super(cause);
    }
}
