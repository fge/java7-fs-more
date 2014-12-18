package com.github.fge.filesystem;

import com.github.fge.filesystem.exception.UnresolvablePathException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.FileSystem;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;

/**
 * {@link Path} utility methods
 *
 * <p>Unless otherwise noted, all methods in this class do not accept null
 * arguments and will throw a {@link NullPointerException} if a null argument
 * is passed to them.</p>
 */
@ParametersAreNonnullByDefault
public final class MorePaths
{
    private MorePaths()
    {
        throw new Error("nice try!");
    }

    /**
     * Resolve a path against another path with a potentially different
     * {@link FileSystem} or {@link FileSystemProvider}
     *
     * <p>{@link Path#resolve(Path)} will refuse to operate if its argument is
     * issued from a different provider (with a {@link
     * ProviderMismatchException}); moreover, if the argument is issued from the
     * same provider but is on a different filesystem, the result of the
     * resolution may be on the argument's filesystem, not the caller's.</p>
     *
     * <p>This method will attempt to resolve the second path against the first
     * so that the result is <em>always</em> associated to the filesystem (and
     * therefore provider) of the first argument. For the resolution to operate,
     * the following conditions must be met for {@code path2}:</p>
     *
     * <ul>
     *     <li>if it is not absolute, it must not have a root;</li>
     *     <li>if it is absolute, it must have a root, and the string
     *     representation of this root must match a string representation of one
     *     possible root of the first path's filesystem.</li>
     * </ul>
     *
     * <p>If the conditions above are not satisfied, this method throws an
     * {@link UnresolvablePathException} (unchecked).</p>
     *
     * <p>If both paths are issued from the same filesystem, this method will
     * delegate to {@code path1}'s {@code .resolve()}; if they are from
     * different filesystems but share the same provider, this method returns:
     * </p>
     *
     * <pre>
     *     path1.resolve(path1.getFileSystem().getPath(path2.toString()))
     * </pre>
     *
     * <p>This means that for instance it is possible to resolve a Unix path
     * against a Windows path, or the reverse, as long as the second path is
     * not absolute (the root paths of both filesystems are incompatible):</p>
     *
     * <ul>
     *     <li>resolving {@code foo/bar/baz} against {@code c:} will return
     *     {@code c:\foo\bar\baz};</li>
     *     <li>resolving {@code baz\quux} against {@code /foo/bar} will return
     *     {@code /foo/bar/baz/quux}.</li>
     * </ul>
     *
     * @param path1 the first path
     * @param path2 the second path
     * @return the resolved path
     * @throws UnresolvablePathException see description
     * @throws InvalidPathException {@code path2} is from a different provider,
     * and one of its name elements is invalid according to {@code path1}'s
     * filesystem
     *
     * @see FileSystem#getPath(String, String...)
     * @see FileSystem#getRootDirectories()
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

        final boolean isAbsolute = path2.isAbsolute();
        final Path root2 = path2.getRoot();

        final String errmsg = isAbsolute
            ? "path to resolve is absolute but has no root"
            : "path to resolve is not absolute but has a root";

        // Always tricky to read an xor...
        if (isAbsolute ^ root2 != null)
            throw new UnresolvablePathException(errmsg);

        Path ret;

        if (isAbsolute) {
            /*
             * Check if the root of path2 is compatible with path1
             */
            final String path2Root = root2.toString();

            boolean foundRoot = false;

            for (final Path root1: fs1.getRootDirectories())
                if (root1.toString().equals(path2Root))
                    foundRoot = true;

            if (!foundRoot)
                throw new UnresolvablePathException("root of path to resolve "
                    + "is incompatible with source path");

            ret = fs1.getPath(path2Root);
        } else {
            /*
             * Since the empty path is defined as having one empty name
             * component, which is rather awkward, we don't want to take the
             * risk of triggering bugs in FileSystem#getPath(); instead, check
             * that the string representation of path2 is empty, and if it is,
             * just return path1.
             */
            if (path2.toString().isEmpty())
                return path1;

            ret = path1;
        }

        for (final Path element: path2)
            ret = ret.resolve(element.toString());

        return ret;
    }

}
