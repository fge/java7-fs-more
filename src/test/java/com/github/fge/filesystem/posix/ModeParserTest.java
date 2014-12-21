package com.github.fge.filesystem.posix;


import com.github.fge.filesystem.exceptions.InvalidModeInstructionException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;

public final class ModeParserTest
{
    @DataProvider
    public Iterator<Object[]> invalidModeInstructions()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[]{ "" });
        list.add(new Object[]{ "ur" });

        return list.iterator();
    }

    @Test(dataProvider = "invalidModeInstructions")
    public void invalidModeInstructionThrowsAppropriateException(
        final String instruction)
    {
        final Set<PosixFilePermission> toAdd
            = EnumSet.noneOf(PosixFilePermission.class);
        final Set<PosixFilePermission> toRemove
            = EnumSet.noneOf(PosixFilePermission.class);

        try {
            ModeParser.parseOne(instruction, toAdd, toRemove);
            shouldHaveThrown(InvalidModeInstructionException.class);
        } catch (InvalidModeInstructionException e) {
            assertThat(e).isExactlyInstanceOf(
                InvalidModeInstructionException.class).hasMessage(instruction);
        }
    }
}
