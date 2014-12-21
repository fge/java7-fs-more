package com.github.fge.filesystem.posix;

import com.github.fge.filesystem.exceptions.InvalidIntModeException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;

public final class PosixModesTest
{
    @Test
    public void outOfRangeNumberThrowsIllegalArgumentException()
    {
        try {
            PosixModes.intModeToPosix(-1);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).isExactlyInstanceOf(InvalidIntModeException.class);
        }

        try {
            PosixModes.intModeToPosix(01000);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).isExactlyInstanceOf(InvalidIntModeException.class);
        }
    }

    @SuppressWarnings("AutoBoxing")
    @DataProvider
    public Iterator<Object[]> intModeTestData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { 0755, "rwxr-xr-x"});
        list.add(new Object[] { 0,    "---------"});
        list.add(new Object[] { 0640, "rw-r-----"});
        list.add(new Object[] { 0404, "r-----r--"});
        list.add(new Object[] { 0071, "---rwx--x"});

        return list.iterator();
    }

    @Test(dataProvider = "intModeTestData")
    public void translatingToPosixPermissionsWorks(final int intMode,
        final String asString)
    {
        final Set<PosixFilePermission> expected
            = PosixFilePermissions.fromString(asString);

        assertThat(PosixModes.intModeToPosix(intMode))
            .as("integer mode is correctly translated")
            .isEqualTo(expected);
    }
}