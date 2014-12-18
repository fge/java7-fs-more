package com.github.fge.filesystem.deletion;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;

@ParametersAreNonnullByDefault
public final class FailFastDeletionVisitor
    implements FileVisitor<Path>
{
    private final FileSystemProvider provider;

    public FailFastDeletionVisitor(final Path victim)
    {
        provider = victim.getFileSystem().provider();
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir,
        final BasicFileAttributes attrs)
        throws IOException
    {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file,
        final BasicFileAttributes attrs)
        throws IOException
    {
        provider.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file,
        final IOException exc)
        throws IOException
    {
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir,
        final IOException exc)
        throws IOException
    {
        provider.delete(dir);
        return FileVisitResult.CONTINUE;
    }

}
