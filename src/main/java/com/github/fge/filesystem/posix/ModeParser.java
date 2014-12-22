package com.github.fge.filesystem.posix;

import com.github.fge.filesystem.exceptions.InvalidModeInstructionException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public final class ModeParser
{
    private static final Pattern COMMA = Pattern.compile(",");

    private ModeParser()
    {
        throw new Error("nice try!");
    }

    public static void parse(final String instructions,
        final Set<PosixFilePermission> toAdd,
        final Set<PosixFilePermission> toRemove)
    {
        for(final String instruction: COMMA.split(instructions))
            parseOne(instruction,toAdd,toRemove);
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

        if(what.isEmpty())
            throw new InvalidModeInstructionException(instruction);
        
        modifySet(who, what, set, instruction);
    }
    
    

    private static void modifySet(final String who, final String what,
        final Set<PosixFilePermission> set, final String instruction)
    {
        final int whoLength = who.length();
        final int whatLength = what.length();

        int whoOrdinal, whatOrdinal;

        for (int i = 0; i < whoLength; i++) {
            whoOrdinal = 0;
            switch(who.charAt(i)) {
                case 'o':
                    whoOrdinal++;
                    /* fall through */
                case 'g':
                    whoOrdinal++;
                    /* fall through */
                case 'u':
                    break;
                case 'a':
                    throw new UnsupportedOperationException(instruction);
                default:
                    throw new InvalidModeInstructionException(instruction);
            }
            for (int j = 0; j < whatLength; j++) {
                whatOrdinal = 3 * whoOrdinal;
                switch(what.charAt(j)) {
                    case 'x':
                        whatOrdinal++;
                        /* fall through */
                    case 'w':
                        whatOrdinal++;
                        /* fall through */
                    case 'r':
                        break;
                    case 'X':
                        throw new UnsupportedOperationException(instruction);
                    default:
                        throw new InvalidModeInstructionException(instruction);
                }
                //add to set
                set.add(PosixModes.PERMISSIONS[whatOrdinal]);
            }
        }
    }
}
