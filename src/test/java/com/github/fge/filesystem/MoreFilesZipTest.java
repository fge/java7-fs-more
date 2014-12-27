package com.github.fge.filesystem;


import com.github.fge.filesystem.exceptions.IsDirectoryException;
import com.github.fge.filesystem.helpers.CustomAssertions;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import com.sun.nio.zipfs.ZipFileSystem;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
    private FileSystemProvider provider;
    private Path path;
    private Path fileNotExist;
    private Path symbolicLink;
    private Path directory;
    private Path zipFilePath;

    @BeforeClass
    public void initFs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux().build("MoreFilesZipTest");
        provider = mock(FileSystemProvider.class);
        path = fs.getPath("existing.zip");
        fileNotExist = fs.getPath("NonExisting.zip");
        zipFilePath = fs.getPath("zipFile.zip");
        URI uri = URI.create("jar:" + zipFilePath.toUri().toString());
        /*
        final Map<String, String> env
            = Collections.singletonMap("create", "true");
        try (
            final FileSystem zipfs = FileSystems.newFileSystem(zipFile, env);
        ) {
           Files.createFile(zipfs.getPath(zipFile.toString()));
        }*/
        symbolicLink = fs.getPath("symbolic.zip");
        directory = fs.getPath("dir");
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
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(IsDirectoryException.class)
                .hasMessage("refusing to open a directory as a zip file");
        }

    }

    @Test
    public void pathIsAZipFileWithNoCreateOptionThrowsNoSuchFileException()
    {
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
        //TODO: This test is weird. Don't know what to do.
        
        final Map<String, String> env
            = Collections.singletonMap("create", "true");
        URI uri = URI.create("jar:" + zipFilePath.toUri().toString());
        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri,env);
        ) {
            //Path p = zipFilePath.toUri();
            FileTime modifiedTime = Files.getLastModifiedTime(zipfs.getPath
                (uri.toString()));
            byte[] data = new byte[10];
            Files.write(zipFilePath,data);
            assertThat(Files.getLastModifiedTime(zipfs.getPath
                (uri.toString())))
                .isGreaterThan(modifiedTime);
        }
    }

    @Test
    public void pathIsNotAnExistingZipFileWithCreateOptionReturnsFileSystem()
        throws IOException
    {
        FileSystem f = MoreFiles.openZip(path, StandardOpenOption.CREATE);
        assertThat(f).isNotEqualTo(fs);
    }

    @AfterClass
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}