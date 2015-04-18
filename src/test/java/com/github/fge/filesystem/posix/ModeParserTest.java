package com.github.fge.filesystem.posix;


import com.github.fge.filesystem.exceptions.InvalidModeInstructionException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

public final class ModeParserTest
{
    private static final Set<PosixFilePermission> NO_PERMISSIONS
            = EnumSet.noneOf(PosixFilePermission.class);
            
    @DataProvider
    public Iterator<Object[]> invalidModeInstructions()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[]{ "" });
        list.add(new Object[]{ "ur" });
        list.add(new Object[]{ "u+" });
        list.add(new Object[]{ "t+r" });
        list.add(new Object[]{ "uw-r" });
        list.add(new Object[]{ "u-y" });

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
            assertThat(e)
                .isExactlyInstanceOf(InvalidModeInstructionException.class)
                .hasMessage(instruction);
        }
    }

    @Test
    public void unsupportedModeInstructionThrowsAppropriateException()
    {
        final Set<PosixFilePermission> toAdd
            = EnumSet.noneOf(PosixFilePermission.class);
        final Set<PosixFilePermission> toRemove
            = EnumSet.noneOf(PosixFilePermission.class);

        String instruction;

        instruction = "u-X";

        try {
            ModeParser.parseOne(instruction, toAdd, toRemove);
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e)
                .isExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessage(instruction);
        }

        instruction = "a+r";

        try {
            ModeParser.parseOne(instruction, toAdd, toRemove);
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e)
                .isExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessage(instruction);
        }
    }

    @DataProvider
    public Iterator<Object[]> validModeInstructions()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] {
            "ug+r",
            EnumSet.of(OWNER_READ, GROUP_READ),
            NO_PERMISSIONS
        });
        list.add(new Object[] {
            "ug-r",
            NO_PERMISSIONS,
            EnumSet.of(OWNER_READ, GROUP_READ)
        });
        list.add(new Object[] {
            "o-x",
            NO_PERMISSIONS,
            EnumSet.of(OTHERS_EXECUTE)
        });
        list.add(new Object[] {
            "uog-rxw",
            NO_PERMISSIONS,
            EnumSet.allOf(PosixFilePermission.class)
        });

        return list.iterator();
    }

    @Test(dataProvider = "validModeInstructions")
    public void validModeInstructionAddsInstructionsInAppropriateSets(
        final String instruction, final Set<PosixFilePermission> add,
        final Set<PosixFilePermission> remove)
    {

        final Set<PosixFilePermission> toAdd
                = EnumSet.noneOf(PosixFilePermission.class);
        final Set<PosixFilePermission> toRemove
                = EnumSet.noneOf(PosixFilePermission.class);

        ModeParser.parseOne(instruction, toAdd, toRemove);

        assertThat(toAdd).isEqualTo(add);
        assertThat(toRemove).isEqualTo(remove);
    }


    @DataProvider
    public Iterator<Object[]> multipleInstructions()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] {
                "ug+r,ou-x",
                EnumSet.of(OWNER_READ, GROUP_READ),
                EnumSet.of(OTHERS_EXECUTE, OWNER_EXECUTE)
        });
        list.add(new Object[] {
                "ug-r,og+xr",
                EnumSet.of(OTHERS_EXECUTE, OTHERS_READ, GROUP_EXECUTE,
                    GROUP_READ),
                EnumSet.of(OWNER_READ, GROUP_READ)
        });

        return list.iterator();
    }

    @Test(dataProvider = "multipleInstructions")
    public void validModeMultipleInstructionAddsInstructionsInAppropriateSets(
            final String instruction, final Set<PosixFilePermission> add,
            final Set<PosixFilePermission> remove)
    {

        final Set<PosixFilePermission> toAdd
            = EnumSet.noneOf(PosixFilePermission.class);
        final Set<PosixFilePermission> toRemove
            = EnumSet.noneOf(PosixFilePermission.class);

        ModeParser.parse(instruction, toAdd, toRemove);

        assertThat(toAdd).isEqualTo(add);
        assertThat(toRemove).isEqualTo(remove);
    }
}
