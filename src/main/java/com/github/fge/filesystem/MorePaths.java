package com.github.fge.filesystem;

import com.github.fge.filesystem.exception.UnresolvablePathException;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
    public static Path resolve(Path path1, Path path2)
    {
        final FileSystem fs1 = path1.getFileSystem();
        final FileSystem fs2 = path2.getFileSystem();

        if (fs1 == fs2)
            return path1.resolve(path2);

        if (fs1.provider() == fs2.provider())
            return path1.resolve(fs1.getPath(path2.toString()));

        if(!path2.isAbsolute())
            return resolvePath2NotAbsolute(path1, path2);

        if (path2.getRoot() == null)
            throw new UnresolvablePathException("path to resolve is absolute but has no root");

        String p2Root = path2.getRoot().toString();
        Iterable<Path> roots = path1.getFileSystem().getRootDirectories();

        for(final Path root: roots) {
            if(root.toString().equals(p2Root)) {// I think I don't need to do root.getRoot().toString() as getRootDirectories() returns the path's root?
                // Do something
            }
        }

        throw new UnresolvablePathException("root of path to resolve is incompatible with source path");
    }

    private static Path resolvePath2NotAbsolute(Path path1, Path path2)
    {
        return null;
    }

}
