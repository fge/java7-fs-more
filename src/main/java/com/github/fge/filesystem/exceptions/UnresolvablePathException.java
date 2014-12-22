package com.github.fge.filesystem.exceptions;

import com.github.fge.filesystem.MorePaths;

import java.nio.file.Path;

/**
 * Exception thrown when a path cannot be resolved against another
 *
 * @see MorePaths#resolve(Path, Path)
 */
@SuppressWarnings("UncheckedExceptionClass")
public final class UnresolvablePathException
    extends UnsupportedOperationException
{
    public UnresolvablePathException()
    {
    }

    public UnresolvablePathException(final String message)
    {
        super(message);
    }

    public UnresolvablePathException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    public UnresolvablePathException(final Throwable cause)
    {
        super(cause);
    }

}
