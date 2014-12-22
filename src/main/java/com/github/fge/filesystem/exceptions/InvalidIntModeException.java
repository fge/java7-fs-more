package com.github.fge.filesystem.exceptions;

import com.github.fge.filesystem.posix.PosixModes;

import java.nio.file.attribute.PosixFilePermission;

/**
 * Exception thrown when an {@code int} cannot be translated to a set of {@link
 * PosixFilePermission}s
 *
 * @see PosixModes#intModeToPosix(int)
 */
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
