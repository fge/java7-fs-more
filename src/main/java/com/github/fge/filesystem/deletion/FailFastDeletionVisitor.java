package com.github.fge.filesystem.deletion;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.RecursionMode;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;

/**
 * Deletion {@link FileVisitor} for {@link RecursionMode#FAIL_FAST fail fast}
 * operation
 *
 * <p>This visitor will fail at the first entry it fails to delete.</p>
 *
 * @see MoreFiles#deleteRecursive(Path, RecursionMode)
 */
@ParametersAreNonnullByDefault
public final class FailFastDeletionVisitor
    implements FileVisitor<Path>
{
    private final FileSystemProvider provider;

    /**
     * Constructor
     *
     * @param victim the path to delete
     */
    public FailFastDeletionVisitor(final Path victim)
    {
        provider = Objects.requireNonNull(victim).getFileSystem().provider();
    }

    @SuppressWarnings("RedundantThrowsDeclaration")
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
