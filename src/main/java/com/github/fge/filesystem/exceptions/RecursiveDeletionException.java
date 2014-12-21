package com.github.fge.filesystem.exceptions;

import java.io.IOException;

public class RecursiveDeletionException
    extends IOException
{

    public RecursiveDeletionException()
    {
    }

    public RecursiveDeletionException(final String message)
    {
        super(message);
    }

    public RecursiveDeletionException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    public RecursiveDeletionException(final Throwable cause)
    {
        super(cause);
    }

}
