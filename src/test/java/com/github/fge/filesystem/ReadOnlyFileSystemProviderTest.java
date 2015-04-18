package com.github.fge.filesystem;


import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fge.filesystem.helpers.CustomAssertions
    .shouldHaveThrown;
import static org.mockito.Mockito.mock;

public class ReadOnlyFileSystemProviderTest
{

    private Path path;
    private FileSystemProvider delegate;
    private ReadOnlyFileSystemProvider provider;

    @BeforeMethod
    public void init ()
        throws IOException
    {
        path = mock(Path.class);
        delegate = mock(FileSystemProvider.class);
        provider = new ReadOnlyFileSystemProvider(delegate);
    }

    @DataProvider
    public Iterator<Object[]> writeAccessOptions()
    {
        final List<Object[]> options = new ArrayList<>();
        options.add(new OpenOption[]{StandardOpenOption.WRITE});
        options.add(new OpenOption[]{StandardOpenOption.CREATE_NEW});
        options.add(new OpenOption[]{StandardOpenOption.APPEND});
        return options.iterator();
    }

    @Test
    public void newOutputStreamThrowsReadOnlyFileSystemException ()
        throws IOException
    {
        try {
            provider.newOutputStream(path);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test (dataProvider = "writeAccessOptions")
    public void newFileChannelThrowsReadOnlyFileSystemException (final OpenOption options)
        throws IOException
    {
        try {
            final Set<OpenOption> set = Collections.singleton(options);
            provider.newFileChannel(path, set);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test (dataProvider = "writeAccessOptions")
    public void newByteChannelThrowsReadOnlyFileSystemException (final OpenOption options)
        throws IOException
    {
        try {
            final Set<OpenOption> set = Collections.singleton(options);
            provider.newFileChannel(path, set);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test
    public void createDirectoryThrowsReadOnlyFileSystemException ()
        throws IOException
    {
        try {
            provider.createDirectory(path);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test
    public void deleteThrowsReadOnlyFileSystemException ()
        throws IOException
    {
        try {
            provider.delete(path);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test
    public void copyThrowsReadOnlyFileSystemException ()
        throws IOException
    {
        final Path target = mock(Path.class);
        try {
            provider.copy(path, target);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test
    public void moveThrowsReadOnlyFileSystemException ()
        throws IOException
    {
        final Path target = mock(Path.class);
        try {
            provider.move(path, target);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test (dataProvider = "writeAccessOptions")
    public void newAsynchronousFileChannelThrowsReadOnlyFileSystemException (OpenOption options)

        throws IOException
    {
        try {
            final Set<OpenOption> set = Collections.singleton(options);
            provider.newAsynchronousFileChannel(path, set, null);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test
    public void createSymbolicLinkThrowsReadOnlyFileSystemException ()
        throws IOException
    {
        try {
            final Path target = mock(Path.class);
            provider.createSymbolicLink(path, target);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test
    public void createLinkThrowsReadOnlyFileSystemException ()
        throws IOException
    {
        try {
            final Path existing = mock(Path.class);
            provider.createLink(path, existing);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test
    public void deleteIfExistsThrowsReadOnlyFileSystemException ()
        throws IOException
    {
        try {
            provider.deleteIfExists(path);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }

    @Test
    public void setAttributeThrowsReadOnlyFileSystemException ()

        throws IOException
    {
        try {
            provider.setAttribute(path, null, null, LinkOption.NOFOLLOW_LINKS);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
        }
    }
}
