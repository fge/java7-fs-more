package com.github.fge.filesystem.deletion;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class FailFastDeletionVisitor
    implements FileVisitor<Path>
{

    private Path victim;
    private final FileSystemProvider provider;

    public FailFastDeletionVisitor(final Path victim)
    {
        this.victim = Objects.requireNonNull(victim);
        provider = victim.getFileSystem().provider();
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir,
        final BasicFileAttributes attrs)
        throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FileVisitResult visitFile(final Path file,
        final BasicFileAttributes attrs)
        throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file,
        final IOException exc)
        throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir,
        final IOException exc)
        throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
