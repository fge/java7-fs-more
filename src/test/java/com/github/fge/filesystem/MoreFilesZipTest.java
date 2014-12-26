package com.github.fge.filesystem;


import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.*;

import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MoreFilesZipTest
{
    private FileSystem fs;
    private Path path;
    private Path symbolicLink;

    @BeforeClass
    public void initFs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux().build("MoreFilesZipTest");
        path = fs.getPath("existing.zip");
        symbolicLink = fs.getPath("symbolic.zip");
        Files.createFile(path);
        Files.createSymbolicLink(symbolicLink,path);
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
    public void pathExistsThrowsFileAlreadyExistsExceptionForCREATE_NEW()
    {
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
    public void symlinkPathWithNOFOLLOWLINKOptionThrowsIOException()
    {
        try {
            MoreFiles.openZip(symbolicLink, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e)
                    .isExactlyInstanceOf(IOException.class)
                    .hasMessage("refusing to open a symbolic link as a zip file");
        }

    }

    @AfterClass
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}