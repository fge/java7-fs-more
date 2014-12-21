package com.github.fge.filesystem.exceptions;

import java.io.IOException;

public final class ContentModificationException
    extends IOException
{
    public ContentModificationException()
    {
    }

    public ContentModificationException(final String message)
    {
        super(message);
    }

    public ContentModificationException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    public ContentModificationException(final Throwable cause)
    {
        super(cause);
    }
}
