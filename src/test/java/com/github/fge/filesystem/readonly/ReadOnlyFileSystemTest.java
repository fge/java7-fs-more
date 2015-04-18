package com.github.fge.filesystem.readonly;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.spi.FileSystemProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

public class ReadOnlyFileSystemTest
{
    private ReadOnlyFileSystem readOnlyFileSystem;
    private FileSystem fileSystem;
    private FileSystemProvider provider;

    @BeforeMethod
    public void init()
    {
        fileSystem = mock(FileSystem.class);
        readOnlyFileSystem = new ReadOnlyFileSystem(fileSystem);
    }

    @Test
    public void providerTest()
        throws IOException
    {
        provider = readOnlyFileSystem.provider();
        assertThat(provider).isExactlyInstanceOf(
            ReadOnlyFileSystemProvider.class);
    }

    @Test
    public void isReadOnlyTest()
        throws IOException
    {
        assertTrue(readOnlyFileSystem.isReadOnly());
    }
}
