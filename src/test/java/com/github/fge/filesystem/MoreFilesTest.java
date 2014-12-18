package com.github.fge.filesystem;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fge.filesystem.CustomAssertions.assertThat;

public final class MoreFilesTest
{
    @SuppressWarnings("AutoBoxing")
    @DataProvider
    public Iterator<Object[]> intModeTestData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { 0755, "rwxr-xr-x"});
        list.add(new Object[] { 0,    "---------"});
        list.add(new Object[] { 0640, "rw-r-----"});
        list.add(new Object[] { 0404, "r-----r--"});

        return list.iterator();
    }

    @Test(dataProvider = "intModeTestData")
    public void translatingToPosixPermissionsWorks(final int intMode,
        final String asString)
    {
        final Set<PosixFilePermission> expected
            = PosixFilePermissions.fromString(asString);

        assertThat(MoreFiles.intModeToPosix(intMode))
            .as("integer mode is correctly translated")
            .isEqualTo(expected);
    }
}