package com.github.fge.filesystem.readonly;


import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Mockito.mock;

public final class ReadOnlyFileSystemProviderTest
{
    private Path path;
    private FileSystemProvider delegate;
    private ReadOnlyFileSystemProvider provider;

    @BeforeMethod
    public void init ()
    {
        path = mock(Path.class);
        delegate = mock(FileSystemProvider.class);
        provider = new ReadOnlyFileSystemProvider(delegate);
    }

    @DataProvider
    public Iterator<Object[]> writeAccessOptions()
    {
        final List<Object[]> options = new ArrayList<>();

        options.add(new Object[]{ StandardOpenOption.WRITE });
        options.add(new Object[]{ StandardOpenOption.CREATE });
        options.add(new Object[]{ StandardOpenOption.CREATE_NEW });
        options.add(new Object[]{ StandardOpenOption.APPEND });

        return options.iterator();
    }

    @Test
    public void newOutputStreamTest()
        throws IOException
    {
        try {
            provider.newOutputStream(path);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test(dataProvider = "writeAccessOptions")
    public void newFileChannelTest(final OpenOption option)
        throws IOException
    {
        try {
            provider.newFileChannel(path, Collections.singleton(option));
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test (dataProvider = "writeAccessOptions")
    public void newByteChannelTest(final OpenOption option)
        throws IOException
    {
        try {
            provider.newFileChannel(path, Collections.singleton(option));
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test
    public void createDirectoryTest()
        throws IOException
    {
        try {
            provider.createDirectory(path);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test
    public void deleteTest()
        throws IOException
    {
        try {
            provider.delete(path);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test
    public void copyTest()
        throws IOException
    {
        try {
            provider.copy(path, mock(Path.class));
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test
    public void moveTest()
        throws IOException
    {
        try {
            provider.move(path, mock(Path.class));
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test(dataProvider = "writeAccessOptions")
    public void newAsynchronousFileChannelTest(final OpenOption option)
        throws IOException
    {
        try {
            provider.newAsynchronousFileChannel(path,
                Collections.singleton(option), null);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test
    public void createSymbolicLinkTest()
        throws IOException
    {
        try {
            provider.createSymbolicLink(path, mock(Path.class));
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test
    public void createLinkTest()
        throws IOException
    {
        try {
            provider.createLink(path, mock(Path.class));
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test
    public void deleteIfExistsTest()
        throws IOException
    {
        try {
            provider.deleteIfExists(path);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }

    @Test
    public void setAttributeTest()
        throws IOException
    {
        try {
            provider.setAttribute(path, null, null);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException ignored) {
        }
    }
}
