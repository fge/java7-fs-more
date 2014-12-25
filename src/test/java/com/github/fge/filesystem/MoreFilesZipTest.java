package com.github.fge.filesystem;


import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermissions;

import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MoreFilesZipTest {
    private FileSystem fs;
    private Path path;

    @BeforeClass
    public void initFs()
            throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux()
                .build("MoreFilesTest");
        path = fs.getPath("existing.zip");
        Files.createFile(path);

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

    @AfterClass
    public void closeFs()
            throws IOException
    {
        fs.close();
    }

}