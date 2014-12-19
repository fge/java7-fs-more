package com.github.fge.filesystem;

import com.github.fge.filesystem.deletion.DeletionMode;
import com.github.fge.filesystem.deletion.FailFastDeletionVisitor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
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

    @Nonnull
    public static Set<PosixFilePermission> intModeToPosix(int intMode)
    {
        if ((intMode & INT_MODE_MAX) != intMode)
            throw new IllegalArgumentException("invalid numeric specification"
                + " for posix permissions");

        final Set<PosixFilePermission> set
            = EnumSet.noneOf(PosixFilePermission.class);

        for (int i = 0; i < PERMISSIONS_LENGTH; i++) {
            if ((intMode & 1) == 1)
                set.add(PERMISSIONS[PERMISSIONS_LENGTH - i - 1]);
            intMode >>= 1;
        }

        return set;
    }
}
