package com.github.fge.filesystem;

import com.github.fge.filesystem.exceptions.IsDirectoryException;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;

import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class MoreFilesZipTest
{
    private FileSystem fs;

    @BeforeMethod
    public void initFs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux().build("MoreFilesZipTest");
    }

    @Test
    public void openZipWillNotAcceptUnsupportedOptions()
        throws IOException
    {
        try {
            MoreFiles.openZip(mock(Path.class), mock(OpenOption.class));
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e)
                .isExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessage("option is not supported");
        }
    }

    @Test
    public void createNewThrowsFileAlreadyExistsExceptionIfZipExists()
        throws IOException
    {
        final Path path = fs.getPath("existing.zip");

        Files.createFile(path);

        try {
            MoreFiles.openZip(path, StandardOpenOption.CREATE_NEW);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e)
                .isExactlyInstanceOf(FileAlreadyExistsException.class)
                .hasMessage(path.toString());
        }

    }

    @Test
    public void noFollowLinksFailToCreateZipIfPathIsSymbolicLink()
        throws IOException
    {
        final Path path = fs.getPath("existing.zip");
        final Path symbolicLink = fs.getPath("symbolic.zip");

        Files.createFile(path);
        Files.createSymbolicLink(symbolicLink, path);

        try {
            MoreFiles.openZip(symbolicLink, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e)
                .isExactlyInstanceOf(IOException.class)
                .hasMessage("refusing to open a symbolic link as a zip file");
        }

    }

    @Test
    public void willNotCreateZipIfPathIsDirectory()
        throws IOException
    {
        final Path directory = fs.getPath("dir");
        Files.createDirectory(directory);

        try {
            MoreFiles.openZip(directory, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e)
                .isExactlyInstanceOf(IsDirectoryException.class)
                .hasMessage("refusing to open a directory as a zip file");
        }

    }

    @Test
    public void nonExistingZipNotCreatedIfCreateNewNotSpecified()
    {
        final Path fileNotExist = fs.getPath("NonExisting.zip");
        try {
            MoreFiles.openZip(fileNotExist, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e)
                .isExactlyInstanceOf(NoSuchFileException.class)
                .hasMessage("no such zip file");
        }

    }

    @Test
    public void createdFileSystemIsToTheRequiredZipFile()
        throws IOException
    {
        final String fileName = "/zipFile.zip";
        final Path zipFilePath = fs.getPath("zipPathFile.zip");

        Path path;

        try (
            final FileSystem zipfs
                = MoreFiles.openZip(zipFilePath, StandardOpenOption.CREATE);
        ) {
            path = zipfs.getPath(fileName);
            Files.createFile(path); //This line is throwing exception
        }

        final URI uri = URI.create("jar:" + zipFilePath.toUri());
        final Map<String, ?> env = Collections.singletonMap("readonly", "true");

        try (
            final FileSystem newfs = FileSystems.newFileSystem(uri, env);
        ) {
            path = newfs.getPath(fileName);
            assertThat(Files.exists(path)).isTrue();
        }
    }

    @AfterMethod
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}
