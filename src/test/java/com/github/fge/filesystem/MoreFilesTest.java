package com.github.fge.filesystem;

import com.github.fge.filesystem.helpers.CustomSoftAssertions;
import com.github.fge.filesystem.helpers.PathAssert;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermissions;

import static com.github.fge.filesystem.helpers.CustomAssertions.assertThat;

public final class MoreFilesTest
{
    private FileSystem fs;
    private FileTime fileTime;

    @BeforeClass
    public void initFs()
        throws IOException
    {
        // TODO: umask's meaning is INVERTED :(
        fs = MemoryFileSystemBuilder.newLinux()
            .setUmask(PosixFilePermissions.fromString("rwx------"))
            .build("MoreFilesTest");
        fileTime = FileTime.fromMillis(System.currentTimeMillis()-10000);
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

        Path path = target;
        soft.assertThat(path).exists().isDirectory()
            .hasPosixPermissions(permString);
        path = path.getParent();
        soft.assertThat(path).exists().isDirectory()
            .hasPosixPermissions(permString);
        path = path.getParent();
        soft.assertThat(path).exists().isDirectory()
            .hasPosixPermissions(permString);

        soft.assertAll();
    }

    @Test(dependsOnMethods = "createDirectoriesWillCreateParents")
    public void createDirectoriesWillNotAttemptToRecreateExistingDirs()
        throws IOException
    {
        final Path target = fs.getPath("/dir1/dir2/dir3");

        MoreFiles.createDirectories(target, "rwx------");

        assertThat(target).exists().isDirectory()
            .hasPosixPermissions("rwxr-xr-x");
    }
    
    public void testTouch() throws IOException {
    	Path filePath = fs.getPath("text.txt");
    	Path created  = MoreFiles.touch(filePath);
    	assertThat(filePath).exists().isNotNull();
    	assertThat(created).exists().isNotNull().isEqualTo(filePath);	
    }

    @AfterClass
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}