package com.github.fge.filesystem;

import com.github.fge.filesystem.exception.UnresolvablePathException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class MorePaths
{
    private MorePaths()
    {
        throw new Error("nice try!");
    }

    /**
     * @param path1
     * @param path2
     * @return
     */
    @SuppressWarnings("ObjectEquality")
    public static Path resolve(final Path path1, final Path path2)
    {
        final FileSystem fs1
            = Objects.requireNonNull(path1).getFileSystem();
        final FileSystem fs2
            = Objects.requireNonNull(path2).getFileSystem();

        if (fs1 == fs2)
            return path1.resolve(path2);

        if (fs1.provider() == fs2.provider())
            return path1.resolve(fs1.getPath(path2.toString()));

        if(!path2.isAbsolute())
            return resolvePath2NotAbsolute(path1, path2);

        if (path2.getRoot() == null)
            throw new UnresolvablePathException("path to resolve is absolute "
                + "but has no root");

        final String p2Root = path2.getRoot().toString();
        final Iterable<Path> roots = path1.getFileSystem().getRootDirectories();

        boolean matchingRoot = false;
        for(final Path root: roots)
            if (root.toString().equals(p2Root))
                matchingRoot = true;

        if (!matchingRoot)
            throw new UnresolvablePathException("root of path to resolve "
                + "is incompatible with source path");

        return null; //TODO
    }

    private static Path resolvePath2NotAbsolute(final Path path1,
        final Path path2)
    {
        return null; // TODO
    }

}
