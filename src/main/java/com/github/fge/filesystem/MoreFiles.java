package com.github.fge.filesystem;

import com.github.fge.filesystem.deletion.DeleteRecursiveOption;
import com.github.fge.filesystem.deletion.FailFastDeletionVisitor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

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
        final DeleteRecursiveOption option)
        throws IOException
    {
        Objects.requireNonNull(victim);
        Objects.requireNonNull(option);

        if (option == DeleteRecursiveOption.KEEP_GOING)
            throw new UnsupportedOperationException("TODO!");

        Files.walkFileTree(victim, new FailFastDeletionVisitor(victim));
    }
}
