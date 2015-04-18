package com.github.fge.filesystem;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ReadOnlyFileSystemProvider
    extends FileSystemProvider
{

    private final FileSystemProvider delegate;
    private static final Set<? extends OpenOption> WRITE_OPTIONS = new HashSet<>(
        Arrays.asList(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE,
            StandardOpenOption.APPEND));

    public ReadOnlyFileSystemProvider(final FileSystemProvider delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public String getScheme()
    {
        return delegate.getScheme();
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env)
        throws IOException
    {
        return delegate.newFileSystem(uri, env);
    }

    @Override
    public FileSystem getFileSystem(URI uri)
    {
        return delegate.getFileSystem(uri);
    }

    @Override
    public Path getPath(URI uri)
    {
        return delegate.getPath(uri);
    }

    @Override
    public FileSystem newFileSystem(Path path, Map<String, ?> env)
        throws IOException
    {
        return delegate.newFileSystem(path, env);
    }

    @Override
    public InputStream newInputStream(Path path, OpenOption... options)
        throws IOException
    {
        return delegate.newInputStream(path, options);
    }

    @Override
    public OutputStream newOutputStream(Path path, OpenOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public FileChannel newFileChannel(Path path,
        Set<? extends OpenOption> options, FileAttribute<?>... attrs)
        throws IOException
    {
        if (isWriteAccess (options))
            throw new ReadOnlyFileSystemException();
        return delegate.newFileChannel(path, options, attrs);
    }

    @Override
    public AsynchronousFileChannel newAsynchronousFileChannel(Path path,
        Set<? extends OpenOption> options, ExecutorService executor,
        FileAttribute<?>... attrs)
        throws IOException
    {
        if (isWriteAccess (options))
            throw new ReadOnlyFileSystemException();
        return delegate.newAsynchronousFileChannel(path, options, executor,
            attrs);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path,
        Set<? extends OpenOption> options, FileAttribute<?>... attrs)
        throws IOException
    {
        if (isWriteAccess (options))
            throw new ReadOnlyFileSystemException();
        return delegate.newByteChannel(path, options, attrs);
    }


    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir,
        DirectoryStream.Filter<? super Path> filter)
        throws IOException
    {
        return delegate.newDirectoryStream(dir, filter);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void createSymbolicLink(Path link, Path target,
        FileAttribute<?>... attrs)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void createLink(Path link, Path existing)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void delete(Path path)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public boolean deleteIfExists(Path path)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public Path readSymbolicLink(Path link)
        throws IOException
    {
        return delegate.readSymbolicLink(link);
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void move(Path source, Path target, CopyOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public boolean isSameFile(Path path, Path path2)
        throws IOException
    {
        return delegate.isSameFile(path, path2);
    }

    @Override
    public boolean isHidden(Path path)
        throws IOException
    {
        return delegate.isHidden(path);
    }

    @Override
    public FileStore getFileStore(Path path)
        throws IOException
    {
        return delegate.getFileStore(path);
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes)
        throws IOException
    {
        delegate.checkAccess(path, modes);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path,
        Class<V> type, LinkOption... options)
    {
        return delegate.getFileAttributeView(path, type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path,
        Class<A> type, LinkOption... options)
        throws IOException
    {
        return delegate.readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes,
        LinkOption... options)
        throws IOException
    {
        return delegate.readAttributes(path, attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value,
        LinkOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    private static boolean isWriteAccess (Set<? extends OpenOption> options) {
        for (OpenOption option : options) {
            if (WRITE_OPTIONS.contains(option)) {
                return true;
            }
        }
        return false;
    }

}
