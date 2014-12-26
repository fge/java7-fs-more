package com.github.fge.filesystem;


import com.github.fge.filesystem.exceptions.IsDirectoryException;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static com.github.fge.filesystem.helpers.CustomAssertions
    .shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class MoreFilesZipTest
{
    private FileSystem fs;
    private Path path;
    private Path symbolicLink;
    private Path directory;

    @BeforeClass
    public void initFs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux().build("MoreFilesZipTest");
        path = fs.getPath("existing.zip");
        symbolicLink = fs.getPath("symbolic.zip");
        directory = fs.getPath("dir");
        Files.createFile(path);
        Files.createSymbolicLink(symbolicLink, path);
        Files.createDirectory(directory);
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
    public void pathExistsThrowsFileAlreadyExistsExceptionForCREATE_NEW()
    {
        try {
            MoreFiles.openZip(path, StandardOpenOption.CREATE_NEW);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(FileAlreadyExistsException.class)
                .hasMessage(path.toString());
        }

    }

    @Test
    public void symlinkPathWithNOFOLLOWLINKOptionThrowsIOException()
    {
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
        try {
            MoreFiles.openZip(directory, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(IsDirectoryException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(IsDirectoryException.class)
                .hasMessage("refusing to open a directory as a zip file");
        }

    }

    @AfterClass
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}