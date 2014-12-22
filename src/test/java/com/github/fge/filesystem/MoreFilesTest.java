package com.github.fge.filesystem;

import com.github.fge.filesystem.helpers.CustomSoftAssertions;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermissions;

import static com.github.fge.filesystem.helpers.CustomAssertions.assertThat;

public final class MoreFilesTest
{
    private FileSystem fs;

    private FileTime fileTime;
    private Path path;

    @BeforeClass
    public void initFs()
        throws IOException
    {
        // TODO: umask's meaning is INVERTED :(
        fs = MemoryFileSystemBuilder.newLinux()
            .setUmask(PosixFilePermissions.fromString("rwx------"))
            .build("MoreFilesTest");
        fileTime = FileTime.fromMillis(System.currentTimeMillis() - 10_000L);
        path = fs.getPath("/existing");
        Files.createFile(path);
    }

    @Test
    public void createFileIgnoresUmask()
        throws IOException
    {
        final String permstring = "rw-rw-rw-";

        final Path createdFile = fs.getPath("/createdFile");
        final Path actual = MoreFiles.createFile(createdFile, permstring);

        assertThat(actual).isNotNull();
        assertThat(actual).exists().isRegularFile()
            .hasPosixPermissions(permstring);
    }

    @Test
    public void createDirectoryIgnoresUmask()
        throws IOException
    {
        final String permstring = "rwxr-x---";

        final Path dir = fs.getPath("/dir");
        final Path actual = MoreFiles.createDirectory(dir, permstring);

        assertThat(actual).isNotNull();
        assertThat(actual).exists().isDirectory()
            .hasPosixPermissions(permstring);
    }


    @Test
    public void createDirectoriesWillCreateParents()
        throws IOException
    {
        final Path target = fs.getPath("/dir1/dir2/dir3");

        final String permString = "rwxr-xr-x";
        MoreFiles.createDirectories(target, permString);

        final CustomSoftAssertions soft = CustomSoftAssertions.create();

        Path tmp = target;
        soft.assertThat(tmp).exists().isDirectory()
            .hasPosixPermissions(permString);
        tmp = tmp.getParent();
        soft.assertThat(tmp).exists().isDirectory()
            .hasPosixPermissions(permString);
        tmp = tmp.getParent();
        soft.assertThat(tmp).exists().isDirectory()
            .hasPosixPermissions(permString);

        soft.assertAll();
    }

    @Test(dependsOnMethods = "createDirectoriesWillCreateParents")
    public void createDirectoriesWillNotAttemptToRecreateExistingDirs()
        throws IOException
    {
        final Path target = fs.getPath("/dir1/dir2/dir3");

        final Path actual
            = MoreFiles.createDirectories(target, "rwx------");

        assertThat(actual).isNotNull().exists().isDirectory()
            .hasPosixPermissions("rwxr-xr-x").isEqualTo(target);
    }

    @Test
    public void touchCreatesNonExistingEntryAsARegularFile() throws IOException
    {
        final Path filePath = fs.getPath("text.txt");
        final Path created  = MoreFiles.touch(filePath);

        assertThat(created).isNotNull().isRegularFile().isEqualTo(filePath);
    }


    @Test
    public void setTimeToFileTest()
        throws IOException
    {
        final Path modified = MoreFiles.setTimes(path, fileTime);
        assertThat(modified).isNotNull().exists().hasAccessTime(fileTime)
            .hasModificationTime(fileTime).isEqualTo(path);
    }

    @AfterClass
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}