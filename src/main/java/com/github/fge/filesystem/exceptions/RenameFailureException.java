package com.github.fge.filesystem.exceptions;

import java.io.IOException;

public final class RenameFailureException
    extends IOException
{
    public RenameFailureException()
    {
    }

    public RenameFailureException(final String message)
    {
        super(message);
    }

    public RenameFailureException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public RenameFailureException(final Throwable cause)
    {
        super(cause);
    }
}
