package com.github.fge.filesystem;

import com.github.fge.filesystem.deletion.DeletionMode;
import com.github.fge.filesystem.deletion.FailFastDeletionVisitor;
import com.github.fge.filesystem.exceptions.InvalidIntModeException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

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
    // TODO: hardcoded; but POSIX permissions will not change anytime soon
    private static final PosixFilePermission[] PERMISSIONS
        = PosixFilePermission.values();
    private static final int PERMISSIONS_LENGTH = PERMISSIONS.length;
    private static final int INT_MODE_MAX = (1 << PERMISSIONS_LENGTH) - 1;

    private MoreFiles()
    {
        throw new Error("nice try!");
    }

    public static void deleteRecursive(final Path victim,
        final DeletionMode option)
        throws IOException
    {
        Objects.requireNonNull(victim);
        Objects.requireNonNull(option);

        if (option == DeletionMode.KEEP_GOING)
            throw new UnsupportedOperationException("TODO!");

        Files.walkFileTree(victim, new FailFastDeletionVisitor(victim));
    }

    /**
     * Convert an integer into a set of {@link PosixFilePermission}s
     *
     * <p>Note that this method will not try and read {@code 755} "in octal";
     * you <strong>must</strong> prefix your integer with {@code 0} so that the
     * constant be octal, as in {@code 0755}.</p>
     *
     * @param intMode the mode
     * @return a set of POSIX permissions
     * @throws InvalidIntModeException invalid integer mode
     *
     * @see Files#setPosixFilePermissions(Path, Set)
     */
    @Nonnull
    public static Set<PosixFilePermission> intModeToPosix(int intMode)
    {
        if ((intMode & INT_MODE_MAX) != intMode)
            throw new InvalidIntModeException();

        final Set<PosixFilePermission> set
            = EnumSet.noneOf(PosixFilePermission.class);

        for (int i = 0; i < PERMISSIONS_LENGTH; i++) {
            if ((intMode & 1) == 1)
                set.add(PERMISSIONS[PERMISSIONS_LENGTH - i - 1]);
            intMode >>= 1;
        }

        return set;
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

        Files.createFile(path);
        return Files.setPosixFilePermissions(path, perms);
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

        Files.createDirectory(dir);
        return Files.setPosixFilePermissions(dir, perms);
    }
}
