package com.github.fge.filesystem.copy;

import com.github.fge.filesystem.MorePaths;
import com.github.fge.filesystem.exceptions.RecursiveOperationException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@ParametersAreNonnullByDefault
public final class KeepGoingCopyVisitor
    implements FileVisitor<Path>
{
    private final RecursiveOperationException exception;
    private final Path src;
    private final Path dst;

    private Path currentSrc;
    private Path currentDst;

    public KeepGoingCopyVisitor(final Path src, final Path dst,
        final RecursiveOperationException exception)
    {
        this.exception = exception;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir,
        final BasicFileAttributes attrs)
        throws IOException
    {
        currentSrc = src.relativize(dir);
        currentDst = MorePaths.resolve(dst, currentSrc);
        // We must take empty path into account.
        // Note that the destination directory will have been created for us.
        try {
            Files.createDirectories(currentDst);
        } catch (IOException e) {
            exception.addSuppressed(e);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file,
        final BasicFileAttributes attrs)
        throws IOException
    {
        if (!attrs.isRegularFile())
            throw new UnsupportedOperationException();
        currentSrc = src.relativize(file);
        currentDst = MorePaths.resolve(dst, currentSrc);
        try {
            Files.copy(file, currentDst);
        } catch (IOException e) {
            exception.addSuppressed(e);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file,
        final IOException exc)
        throws IOException
    {
        exception.addSuppressed(exc);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir,
        @Nullable final IOException exc)
        throws IOException
    {
        if (exc != null)
            exception.addSuppressed(exc);
        return FileVisitResult.CONTINUE;
    }
}
