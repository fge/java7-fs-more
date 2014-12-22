package com.github.fge.filesystem.exceptions;


import com.github.fge.filesystem.posix.ModeParser;

import java.util.Set;

/**
 * Exception thrown when a mode change instruction string is illegal
 *
 * @see ModeParser#parse(String, Set, Set)
 */
@SuppressWarnings("UncheckedExceptionClass")
public final class InvalidModeInstructionException
    extends IllegalArgumentException
{
    public InvalidModeInstructionException()
    {
    }

    public InvalidModeInstructionException(final String s)
    {
        super(s);
    }

    public InvalidModeInstructionException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    public InvalidModeInstructionException(final Throwable cause)
    {
        super(cause);
    }
}