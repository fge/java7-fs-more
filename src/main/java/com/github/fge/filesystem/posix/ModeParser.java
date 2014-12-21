package com.github.fge.filesystem.posix;

import com.github.fge.filesystem.exceptions.InvalidModeInstructionException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public final class ModeParser
{
    private static final Pattern PATTERN = Pattern.compile(",");

    private ModeParser()
    {
        throw new Error("nice try!");
    }

    // Visible for testing
    static void parseOne(final String instruction,
        final Set<PosixFilePermission> toAdd,
        final Set<PosixFilePermission> toRemove)
    {
        if (instruction.indexOf('+') < 0 && instruction.indexOf('-') < 0)
            throw new InvalidModeInstructionException(instruction);
    }
}
