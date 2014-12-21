package com.github.fge.filesystem.helpers;

import org.assertj.core.api.ObjectAssert;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
public class PathAssert
    extends ObjectAssert<Path>
{
    public PathAssert(@Nullable final Path actual)
    {
        super(actual);
    }

    public final void doesNotExist()
    {
        isNotNull();
        if (Files.exists(actual))
            failWithMessage("expected " + actual + " not to exist, but it has "
                + "been found");
    }

    public final PathAssert exists()
    {
        isNotNull();
        if (Files.notExists(actual))
            failWithMessage("expected " + actual + " to exist, but it has not "
                + "been found");
        return this;
    }

    public final PathAssert hasPosixPermissions(final String permstring)
        throws IOException
    {
        exists();
        Objects.requireNonNull(permstring);

        final Set<PosixFilePermission> actualPerms = Files
            .getPosixFilePermissions(actual, LinkOption.NOFOLLOW_LINKS);
        final Set<PosixFilePermission> expectedPerms
            = PosixFilePermissions.fromString(permstring);

        if (!actualPerms.equals(expectedPerms))
            failWithMessage("path permissions differ from expectations\n"
                + "\nexpected: <%s>\n\nactual: <%s>",
                PosixFilePermissions.toString(actualPerms), permstring
            );

        return this;
    }

    public final PathAssert isRegularFile()
    {
        exists();

        if (!Files.isRegularFile(actual, LinkOption.NOFOLLOW_LINKS))
            failWithMessage("expected %s to be a regular file", actual);

        return this;
    }

    public final PathAssert isDirectory()
    {
        exists();

        if (!Files.isDirectory(actual, LinkOption.NOFOLLOW_LINKS))
            failWithMessage("expected %s to be a directory", actual);

        return this;
    }

    public final PathAssert hasAccessTime(final FileTime fileTime)
        throws IOException
    {
        exists();
        Objects.requireNonNull(fileTime);

        final BasicFileAttributeView view
            = Files.getFileAttributeView(actual,
            BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        final FileTime time = view.readAttributes().lastAccessTime();

        if (!time.equals(fileTime))
            failWithMessage("Access time differs from expectations\n"
                + "\n expect: <%s>\n actual: <%s>", time, fileTime);

        return this;
    }

    public final PathAssert hasModificationTime(final FileTime fileTime)
        throws IOException
    {
        exists();
        Objects.requireNonNull(fileTime);

        final BasicFileAttributeView view = Files.getFileAttributeView(actual,
            BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        final FileTime time = view.readAttributes().lastModifiedTime();

        if (!time.equals(fileTime))
            failWithMessage("Modification time differ from expectation\n"
                + "\n expect: <%s>\n actual: <%s>", time, fileTime);

        return this;
    }
}
