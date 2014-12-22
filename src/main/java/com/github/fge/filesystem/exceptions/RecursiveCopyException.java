package com.github.fge.filesystem.exceptions;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.RecursionMode;
import com.github.fge.filesystem.copy.KeepGoingCopyVisitor;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;

/**
 * Exception thrown when a recursive copy in {@link RecursionMode#KEEP_GOING
 * keep going} mode fails to complete without errors
 *
 * @see KeepGoingCopyVisitor
 * @see MoreFiles#copyRecursive(Path, Path, RecursionMode, CopyOption...)
 */
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
