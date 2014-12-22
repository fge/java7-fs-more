package com.github.fge.filesystem.deletion;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.RecursionMode;
import com.github.fge.filesystem.exceptions.RecursiveDeletionException;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;

/**
 * Deletion {@link FileVisitor} for {@link RecursionMode#KEEP_GOING keep going}
 * operation
 *
 * <p>This visitor will collect all {@link IOException}s it encounters into the
 * {@link RecursiveDeletionException} argument (as a {@link
 * Throwable#addSuppressed(Throwable) suppressed} exception).</p>
 *
 * @see MoreFiles#deleteRecursive(Path, RecursionMode)
 */
public final class KeepGoingDeletionVisitor
    implements FileVisitor<Path>
{
    private final FileSystemProvider provider;
    private final RecursiveDeletionException exception;

    /**
     * Constructor
     *
     * @param victim the path to delete recursively
     * @param exception the exception to add suppressed exceptions to
     */
    public KeepGoingDeletionVisitor(final Path victim,
        final RecursiveDeletionException exception)
    {
        provider = Objects.requireNonNull(victim).getFileSystem().provider();
        this.exception = Objects.requireNonNull(exception);
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
        try {
            provider.delete(file);
        } catch (IOException ioException) {
            exception.addSuppressed(ioException);
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
        final IOException exc)
        throws IOException
    {
        if (exc != null) {
            exception.addSuppressed(exc);
            return FileVisitResult.CONTINUE;
        }

        try {
            provider.delete(dir);
        } catch (IOException ioException) {
            exception.addSuppressed(ioException);
        }
        return FileVisitResult.CONTINUE;
    }
}
