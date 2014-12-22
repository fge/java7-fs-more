package com.github.fge.filesystem.copy;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.MorePaths;
import com.github.fge.filesystem.RecursionMode;
import com.github.fge.filesystem.exceptions.RecursiveCopyException;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * Recursive copy {@link FileVisitor} for {@link RecursionMode#KEEP_GOING
 * keep going} operation
 *
 * <p>This visitor will collect all {@link IOException}s it encounters and add
 * them (as a {@link Throwable#addSuppressed(Throwable) suppressed} exception)
 * to the {@link RecursiveCopyException} argument.</p>
 *
 * @see MoreFiles#copyRecursive(Path, Path, RecursionMode, CopyOption...)
 */
@ParametersAreNonnullByDefault
public final class KeepGoingCopyVisitor
    implements FileVisitor<Path>
{
    private final RecursiveCopyException exception;
    private final Path src;
    private final Path dst;

    private Path currentSrc;
    private Path currentDst;

    /**
     * Constructor
     *
     * @param src the source to copy recursively
     * @param dst the destination of the copy
     * @param exception the exception to collect other exceptions
     */
    public KeepGoingCopyVisitor(final Path src, final Path dst,
        final RecursiveCopyException exception)
    {
        this.exception = Objects.requireNonNull(exception);
        this.src = Objects.requireNonNull(src);
        this.dst = Objects.requireNonNull(dst);
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
