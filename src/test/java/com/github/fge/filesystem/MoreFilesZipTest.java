package com.github.fge.filesystem;


import com.github.fge.filesystem.exceptions.IsDirectoryException;
import com.github.fge.filesystem.helpers.CustomAssertions;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import com.sun.nio.zipfs.ZipFileSystem;
import org.testng.annotations.*;

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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Map;

import static com.github.fge.filesystem.helpers.CustomAssertions
    .shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
            assertThat(e).isExactlyInstanceOf(
                UnsupportedOperationException.class).hasMessage(
                "option is not supported");
        }
    }

    @Test
    public void pathExistsThrowsFileAlreadyExistsExceptionForCREATE_NEW() throws IOException {
        final Path path = fs.getPath("existing.zip");
        Files.createFile(path);
        try {
            MoreFiles.openZip(path, StandardOpenOption.CREATE_NEW);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(FileAlreadyExistsException.class)
                .hasMessage(path.toString());
        }

    }

    @Test
    public void symlinkPathWithNOFOLLOWLINKOptionThrowsIOException() throws IOException {
        final Path path = fs.getPath("existing.zip");
        final Path symbolicLink = fs.getPath("symbolic.zip");
        Files.createFile(path);
        Files.createSymbolicLink(symbolicLink, path);
        try {
            MoreFiles.openZip(symbolicLink, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(IOException.class).hasMessage(
                "refusing to open a symbolic link as a zip file");
        }

    }

    @Test
    public void pathIsADirectoryThrowsIsDirectoryException()
    {
        final Path directory = fs.getPath("dir");
        try {
            Files.createDirectory(directory);
            MoreFiles.openZip(directory, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(IsDirectoryException.class)
                .hasMessage("refusing to open a directory as a zip file");
        }

    }

    @Test
    public void pathIsAZipFileWithNoCreateOptionThrowsNoSuchFileException()
    {
        final Path fileNotExist = fs.getPath("NonExisting.zip");
        try {
            MoreFiles.openZip(fileNotExist, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(NoSuchFileException.class)
                .hasMessage("no such zip file");
        }

    }

    @Test
    public void pathIsAnExistingZipFileWithCreateOptionReturnsFileSystem() 
        throws IOException
    {
        final String fileName = "/zipFile.zip";
        final Path zipFilePath = fs.getPath("zipPathFile.zip");
        try (
            final FileSystem zipfs = MoreFiles.openZip(zipFilePath, StandardOpenOption.CREATE);
        ) {
            Path pathInZip = zipfs.getPath(fileName);
            Files.createFile(pathInZip); //This line is throwing exception
        }
        final URI uri = URI.create("jar:" + zipFilePath.toUri());
        final Map<String, ?> env = Collections.singletonMap("readonly", "true");
        try (
                final FileSystem newfs = FileSystems.newFileSystem(uri, env);
        ) {
            Path newPath = newfs.getPath(fileName);
            assertThat(newPath.toFile().exists()).isTrue();
        }
    }

    @AfterMethod
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}