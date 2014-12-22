package com.github.fge.filesystem.copy;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.MorePaths;
import com.github.fge.filesystem.RecursionMode;

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
 * Recursive copy {@link FileVisitor} for {@link RecursionMode#FAIL_FAST fail
 * fast} operation
 *
 * <p>This visitor will fail for the first directory it fails to create or the
 * first file it fails to copy.</p>
 *
 * @see MoreFiles#copyRecursive(Path, Path, RecursionMode, CopyOption...)
 */
@ParametersAreNonnullByDefault
public final class FailFastCopyVisitor
    implements FileVisitor<Path>
{
    private final Path src;
    private final Path dst;

    private Path currentSrc;
    private Path currentDst;

    /**
     * Constructor
     *
     * @param src the source path
     * @param dst the destination path
     */
    public FailFastCopyVisitor(final Path src, final Path dst)
    {
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
        Files.createDirectories(currentDst);
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
        Files.copy(file, currentDst);
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
        if (exc != null)
            throw exc;
        return FileVisitResult.CONTINUE;
    }
}
