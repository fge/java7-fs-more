package com.github.fge.filesystem.exceptions;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.RecursionMode;
import com.github.fge.filesystem.deletion.KeepGoingDeletionVisitor;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Exception thrown when a recursice deletion in {@link RecursionMode#KEEP_GOING
 * keep going} mode fails to complete without errors
 *
 * @see KeepGoingDeletionVisitor
 * @see MoreFiles#deleteRecursive(Path, RecursionMode)
 */
public final class RecursiveDeletionException
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
