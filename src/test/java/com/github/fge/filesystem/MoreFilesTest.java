package com.github.fge.filesystem;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public final class MoreFilesTest
{
    private FileSystem fs;

    private FileTime fileTime;
    private Path path;

    @BeforeClass
    public void initFs()
        throws IOException
    {
        // Simulate a umask of 027
        fs = MemoryFileSystemBuilder.newLinux()
            .setUmask(PosixFilePermissions.fromString("----w-rwx"))
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
        final Set<PosixFilePermission> expectedPerms
            = PosixFilePermissions.fromString(permstring);

        final Path createdFile = fs.getPath("/createdFile");
        final Path actual = MoreFiles.createFile(createdFile, permstring);

        assertThat(actual).isNotNull();
        assertThat(actual).exists().isRegularFile();

        final Set<PosixFilePermission> actualPerms
            = Files.getPosixFilePermissions(actual);

        assertThat(actualPerms).isEqualTo(expectedPerms);
    }

    @Test
    public void createDirectoryIgnoresUmask()
        throws IOException
    {
        final String permstring = "rwxr-x---";
        final Set<PosixFilePermission> expectedPerms
            = PosixFilePermissions.fromString(permstring);

        final Path dir = fs.getPath("/dir");
        final Path actual = MoreFiles.createDirectory(dir, permstring);

        assertThat(actual).isNotNull();
        assertThat(actual).exists().isDirectory();

        final Set<PosixFilePermission> actualPerms
            = Files.getPosixFilePermissions(actual);

        assertThat(actualPerms).isEqualTo(expectedPerms);
    }


    @Test
    public void createDirectoriesWillCreateParents()
        throws IOException
    {
        final Path target = fs.getPath("/dir1/dir2/dir3");

        final String permString = "rwxr-xr-x";
        final Set<PosixFilePermission> expectedPerms
            = PosixFilePermissions.fromString(permString);

        MoreFiles.createDirectories(target, permString);

        Path tmp;
        Set<PosixFilePermission> perms;

        try (
            final AutoCloseableSoftAssertions soft
                = new AutoCloseableSoftAssertions();
        ) {
            tmp = target;
            perms = Files.getPosixFilePermissions(tmp);
            soft.assertThat(tmp).exists().isDirectory();
            soft.assertThat(perms).isEqualTo(expectedPerms);
            tmp = tmp.getParent();
            perms = Files.getPosixFilePermissions(tmp);
            soft.assertThat(tmp).exists().isDirectory();
            soft.assertThat(perms).isEqualTo(expectedPerms);
            tmp = tmp.getParent();
            perms = Files.getPosixFilePermissions(tmp);
            soft.assertThat(tmp).exists().isDirectory();
            soft.assertThat(perms).isEqualTo(expectedPerms);
        }
    }

    @Test(dependsOnMethods = "createDirectoriesWillCreateParents")
    public void createDirectoriesWillNotAttemptToRecreateExistingDirs()
        throws IOException
    {
        final Path target = fs.getPath("/dir1/dir2/dir3");

        final Path actual
            = MoreFiles.createDirectories(target, "rwx------");

        final Set<PosixFilePermission> perms
            = PosixFilePermissions.fromString("rwxr-xr-x");

        assertThat(actual).isNotNull().exists().isDirectory();
        assertThat(actual).isEqualTo(target);

        final Set<PosixFilePermission> actualPerms
            = Files.getPosixFilePermissions(actual);

        assertThat(actualPerms).isEqualTo(perms);
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

        assertThat(modified).isNotNull().exists().isEqualTo(path);

        final BasicFileAttributes attrs = Files.readAttributes(modified,
            BasicFileAttributes.class);

        FileTime actualTime;

        actualTime = attrs.lastAccessTime();
        assertThat(actualTime).isEqualTo(fileTime);

        actualTime = attrs.lastModifiedTime();
        assertThat(actualTime).isEqualTo(fileTime);
    }

    @DataProvider
    public Iterator<Object[]> changeModeData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "rw-r--r--", "o-r", "rw-r-----" });
        list.add(new Object[] { "rwxr-xr-x", "go-x,o-r", "rwxr-----" });
        list.add(new Object[] { "rw-r--r--", "g+w,o-r", "rw-rw----" });

        return list.iterator();
    }

    @Test(dataProvider = "changeModeData")
    public void changeModeAltersTargetPermissions(final String before,
        final String instructions, final String after)
        throws IOException
    {
        final Set<PosixFilePermission> initial
            = PosixFilePermissions.fromString(before);
        final Set<PosixFilePermission> expected
            = PosixFilePermissions.fromString(after);

        final Path target = fs.getPath("/target");
        Files.createFile(target);
        Files.setPosixFilePermissions(target, initial);

        final Path ret = MoreFiles.changeMode(target, instructions);

        try (
            final AutoCloseableSoftAssertions soft
                = new AutoCloseableSoftAssertions();
        ) {
            soft.assertThat(ret).isNotNull();

            final Set<PosixFilePermission> actual
                = Files.getPosixFilePermissions(ret);

            soft.assertThat(actual).as("permissions are correctly modified")
                .isEqualTo(expected);

        } finally {
            Files.delete(target);
        }
    }


    @AfterClass
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}
