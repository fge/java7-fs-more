package com.github.fge.filesystem.readonly;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

/**
 * Read only wrapper over an existing {@link FileSystem}
 *
 * <p>This wrapper delegates all methods to the filesystem provided as an
 * argument except for two:</p>
 *
 * <ul>
 *     <li>{@link #isReadOnly()} will always return {@code true};</li>
 *     <li>{@link #provider()} will return the delegate's provider wrapped into
 *     a {@link ReadOnlyFileSystemProvider}.</li>
 * </ul>
 */
public final class ReadOnlyFileSystem
    extends FileSystem
{
    private final FileSystem delegate;
    private final FileSystemProvider provider;

    public ReadOnlyFileSystem(final FileSystem delegate)
    {
        this.delegate = delegate;
        provider = new ReadOnlyFileSystemProvider(delegate.provider());
    }

    @Override
    public FileSystemProvider provider()
    {
        return provider;
    }

    @Override
    public void close()
        throws IOException
    {
        delegate.close();
    }

    @Override
    public boolean isOpen()
    {
        return delegate.isOpen();
    }

    @Override
    public boolean isReadOnly()
    {
        return true;
    }

    @Override
    public String getSeparator()
    {
        return delegate.getSeparator();
    }

    @Override
    public Iterable<Path> getRootDirectories()
    {
        return delegate.getRootDirectories();
    }

    @Override
    public Iterable<FileStore> getFileStores()
    {
        return delegate.getFileStores();
    }

    @Override
    public Set<String> supportedFileAttributeViews()
    {
        return delegate.supportedFileAttributeViews();
    }

    @SuppressWarnings("OverloadedVarargsMethod")
    @Override
    public Path getPath(final String first, final String... more)
    {
        return delegate.getPath(first, more);
    }

    @Override
    public PathMatcher getPathMatcher(final String syntaxAndPattern)
    {
        return delegate.getPathMatcher(syntaxAndPattern);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
    {
        return delegate.getUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService()
        throws IOException
    {
        return delegate.newWatchService();
    }
}
