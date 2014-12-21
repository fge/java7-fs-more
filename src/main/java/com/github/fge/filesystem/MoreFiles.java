package com.github.fge.filesystem;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.github.fge.filesystem.deletion.FailFastDeletionVisitor;
import com.github.fge.filesystem.deletion.KeepGoingDeletionVisitor;
import com.github.fge.filesystem.exceptions.RecursiveDeletionException;
import com.github.fge.filesystem.posix.PosixModes;

/**
 * Utility classes in complement of the JDK's {@link Files}
 *
 * <p>Unless otherwise noted, all methods in this class do not accept null
 * arguments and will throw a {@link NullPointerException} if a null argument
 * is passed to them.</p>
 */
@ParametersAreNonnullByDefault
public final class MoreFiles
{
    private MoreFiles()
    {
        throw new Error("nice try!");
    }

    public static void deleteRecursive(final Path victim,
        final RecursionMode option)
        throws IOException
    {
        Objects.requireNonNull(victim);
        Objects.requireNonNull(option);

        FileVisitor<Path> visitor;

		switch (option) {
		case KEEP_GOING:
			RecursiveDeletionException exception = new RecursiveDeletionException();
			visitor = new KeepGoingDeletionVisitor(victim, exception);
			Files.walkFileTree(victim, visitor);
			if (exception.getSuppressed().length != 0)
				throw exception;
			break;
		case FAIL_FAST:
			visitor = new FailFastDeletionVisitor(victim);
			Files.walkFileTree(victim, visitor);
			break;
		default:
			throw new IllegalStateException();
		}
    }

    @Nonnull
    public static Path setMode(final int mode, final Path path)
        throws IOException
    {
        Objects.requireNonNull(path);
        final Set<PosixFilePermission> perms = PosixModes.intModeToPosix(mode);

        return Files.setPosixFilePermissions(path, perms);
    }

    @Nonnull
    public static Path setMode(final String modeString, final Path path)
        throws IOException
    {
        Objects.requireNonNull(modeString);
        Objects.requireNonNull(path);

        final Set<PosixFilePermission> perms
            = PosixFilePermissions.fromString(modeString);

        return Files.setPosixFilePermissions(path, perms);
    }

    @Nonnull
    public static Path createFile(final Path path,
        final String posixPermissions)
        throws IOException
    {
        Objects.requireNonNull(path);
        Objects.requireNonNull(posixPermissions);

        final Set<PosixFilePermission> perms
            = PosixFilePermissions.fromString(posixPermissions);

        return doCreateFile(path, perms);
    }

    @Nonnull
    public static Path createFile(final Path path, final int mode)
        throws IOException
    {
        Objects.requireNonNull(path);

        final Set<PosixFilePermission> perms = PosixModes.intModeToPosix(mode);

        return doCreateFile(path, perms);
    }

    @Nonnull
    public static Path createDirectory(final Path dir,
        final String posixPermissions)
        throws IOException
    {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(posixPermissions);

        final Set<PosixFilePermission> perms
            = PosixFilePermissions.fromString(posixPermissions);

        return doCreateDirectory(dir, perms);
    }

    @Nonnull
    public static Path createDirectory(final Path dir, final int mode)
        throws IOException
    {
        Objects.requireNonNull(dir);

        final Set<PosixFilePermission> perms = PosixModes.intModeToPosix(mode);

        return doCreateDirectory(dir, perms);
    }

    @Nonnull
    public static Path createDirectories(final Path dir,
        final String posixPermissions)
        throws IOException
    {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(posixPermissions);

        final Path realDir = dir.toAbsolutePath();
        final Set<PosixFilePermission> perms
            = PosixFilePermissions.fromString(posixPermissions);

        doCreateDirectories(realDir, perms);

        return dir;
    }

    @Nonnull
    public static Path createDirectories(final Path dir, final int mode)
        throws IOException
    {
        Objects.requireNonNull(dir);

        final Path realDir = dir.toAbsolutePath();
        final Set<PosixFilePermission> perms = PosixModes.intModeToPosix(mode);

        doCreateDirectories(realDir, perms);

        return dir;
    }

    private static Path doCreateFile(final Path path,
        final Set<PosixFilePermission> perms)
        throws IOException
    {
        Files.createFile(path);
        return Files.setPosixFilePermissions(path, perms);
    }

    private static Path doCreateDirectory(final Path dir,
        final Set<PosixFilePermission> perms)
        throws IOException
    {
        Files.createDirectory(dir);
        return Files.setPosixFilePermissions(dir, perms);
    }

    private static void doCreateDirectories(final Path realDir,
        final Set<PosixFilePermission> perms)
        throws IOException
    {
        final List<Path> created = new ArrayList<>();

        Path parent = realDir;

        while (parent != null && !Files.exists(parent)) {
            created.add(parent);
            parent = parent.getParent();
        }

        Files.createDirectories(realDir);

        for (final Path path: created)
            Files.setPosixFilePermissions(path, perms);
    }
}
