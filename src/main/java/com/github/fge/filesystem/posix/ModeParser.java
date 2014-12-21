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
        final int plus = instruction.indexOf('+');
        final int minus = instruction.indexOf('-');

        if (plus < 0 && minus < 0)
            throw new InvalidModeInstructionException(instruction);

        final String who;
        final String what;
        final Set<PosixFilePermission> set;

        if (plus >= 0) {
            who = plus == 0 ? "ugo" : instruction.substring(0, plus);
            what = instruction.substring(plus + 1);
            set = toAdd;
        } else {
            // If it's not plusIndex it's minusIndex
            who = minus == 0 ? "ugo" : instruction.substring(0, minus);
            what = instruction.substring(minus + 1);
            set = toRemove;
        }

        modifySet(who, what, set);
    }

    private static void modifySet(final String who, final String what,
        final Set<PosixFilePermission> set)
    {
        // TODO
    }
}
