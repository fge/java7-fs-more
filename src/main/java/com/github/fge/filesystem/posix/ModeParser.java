package com.github.fge.filesystem.posix;

import com.github.fge.filesystem.exceptions.InvalidModeInstructionException;
import com.sun.javafx.beans.annotations.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class ModeParser {

    private static final Pattern PATTERN = Pattern.compile(",");

    private ModeParser() {
    }
    
    @NonNull
    static void parseOne(final String instruction, Set<PosixFilePermission> toAdd, Set<PosixFilePermission> toRemove) {
        if(instruction.indexOf('+') < 0 && instruction.indexOf('-') < 0)
            throw new InvalidModeInstructionException(instruction);
    }
    
}
