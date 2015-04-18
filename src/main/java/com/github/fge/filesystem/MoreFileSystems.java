package com.github.fge.filesystem;

import com.github.fge.filesystem.readonly.ReadOnlyFileSystem;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.WillNotClose;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for {@link FileSystem}s
 *
 * <p>This utility class offers methods to:</p>
 *
 * <ul>
 *     <li>open zip files as file systems (therefore including jars, wars etc);
 *     </li>
 *     <li>wrap existing file systems in a read only wrapper.</li>
 * </ul>
 *
 * <p>It is the user's responsibility to {@link FileSystem#close() close} the
 * filesystems returned by these methods.</p>
 *
 * <p>Unless otherwise noted, all methods in this class do not accept null
 * arguments and will throw a {@link NullPointerException} if a null argument
 * is passed to them.</p>
 */
@ParametersAreNonnullByDefault
public final class MoreFileSystems
{
    private static final Map<String, ?> ZIP_NOCREATE = Collections.emptyMap();
    private static final Map<String, ?> ZIP_CREATE
        = Collections.singletonMap("create", "true");

    private MoreFileSystems ()
    {
        throw new Error("nice try!");
    }

    /**
     * Open a zip as a file system, read write or read only
     *
     * <p>Note that if it is determined that the zip file is not writable by the
     * current user, the file system will be read only even if the {@code
     * readOnly} argument is {@code false}.</p>
     *
     * @param path the path to the zip
     * @param readOnly whether the zip should be opened read only
     * @return the filesystem
     * @throws FileSystemNotFoundException zip does not exist
     * @throws IOException failed to open the path as a zip filesystem
     */
    @Nonnull
    @WillNotClose
    public static FileSystem openZip(final Path path, final boolean readOnly)
        throws IOException
    {
        final URI uri = URI.create("jar:" + path.toUri());

        final FileSystem fs = FileSystems.newFileSystem(uri, ZIP_NOCREATE);

        return readOnly ? new ReadOnlyFileSystem(fs) : fs;
    }

    /**
     * Create a new zip file as a filesystem
     *
     * @param path the path to the zip to create
     * @return a filesystem
     * @throws FileAlreadyExistsException zip file already exists
     * @throws IOException other errors
     */
    @Nonnull
    @WillNotClose
    public static FileSystem createZip(final Path path)
        throws IOException
    {
        if (Files.exists(path))
            throw new FileAlreadyExistsException(path.toString());

        final URI uri = URI.create("jar:" + path.toUri());

        return FileSystems.newFileSystem(uri, ZIP_CREATE);
    }

    /**
     * Return a read only version of a filesystem, if necessary
     *
     * <p>If the filesystem given as an argument is already read only (ie,
     * {@link FileSystem#isReadOnly()} returns true), this
     * method will return the filesystem itself.</p>
     *
     * @param fs the filesystem
     * @return a read only filesystem
     */
    @Nonnull
    @WillNotClose
    public static FileSystem readOnly(final FileSystem fs)
    {
        Objects.requireNonNull(fs);

        return fs.isReadOnly() ? fs : new ReadOnlyFileSystem(fs);
    }
}
