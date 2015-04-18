package com.github.fge.filesystem.readonly;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class ReadOnlyFileSystemTest
{
    private FileSystem fs;

    @BeforeMethod
    public void init()
    {
        fs = new ReadOnlyFileSystem(mock(FileSystem.class));
    }

    @Test
    public void providerTest()
    {
        assertThat(fs.provider())
            .isExactlyInstanceOf(ReadOnlyFileSystemProvider.class);
    }

    @Test
    public void isReadOnlyTest()
    {
        assertThat(fs.isReadOnly()).isTrue();
    }
}
