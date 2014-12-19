package com.github.fge.filesystem;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fge.filesystem.helpers.CustomAssertions.assertThat;
import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;

public final class MoreFilesTest
{
    private FileSystem fs;

    @BeforeClass
    public void initFs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux()
            .setUmask(PosixFilePermissions.fromString("rwx------"))
            .build("MoreFilesTest");
    }

    @Test
    public void outOfRangeNumberThrowsIllegalArgumentException()
    {
        try {
            MoreFiles.intModeToPosix(-1);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("invalid numeric specification for posix"
                + " permissions");
        }

        try {
            MoreFiles.intModeToPosix(01000);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("invalid numeric specification for posix"
                + " permissions");
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

        assertThat(MoreFiles.intModeToPosix(intMode))
            .as("integer mode is correctly translated")
            .isEqualTo(expected);
    }

    // Unfortunately, memoryfilesystem doesn't know of umask :(
    @Test(enabled = false)
    public void createFileIgnoresUmask()
        throws IOException
    {
        final String permstring = "rw-rw-rw-";

        final Path createdFile = fs.getPath("/createdFile");
        final Path actual = MoreFiles.createFile(createdFile, permstring);

        assertThat(actual).isNotNull();
        assertThat(actual).exists().hasPosixPermissions(permstring);
    }

    @AfterClass
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}