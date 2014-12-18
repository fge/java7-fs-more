package com.github.fge.filesystem.deletion;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;

/**
 * A fail-fast deletion {@link FileVisitor}
 *
 * <p>This visitor takes a {@link Path} as an argument and will attempt to
 * recursively {@link FileSystemProvider#delete(Path) delete} the path itself
 * and all entries under it (if the provided path is a directory).</p>
 *
 * <p>Symbolic links are <strong>not</strong> followed. If a symbolic link is
 * encountered, it will be deleted, but not its target (if any).</p>
 *
 * @see Files#walkFileTree(Path, FileVisitor)
 * @see FileVisitOption
 */
@ParametersAreNonnullByDefault
public final class FailFastDeletionVisitor
    implements FileVisitor<Path>
{
    private final FileSystemProvider provider;

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
