package com.github.fge.filesystem;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for {@link FileSystem}s
 *
 * <p>Unless otherwise noted, all methods in this class do not accept null
 * arguments and will throw a {@link NullPointerException} if a null argument
 * is passed to them.</p>
 */
@ParametersAreNonnullByDefault
public final class MoreFileSystems
{
    private static final Map<String, ?> ZIP_ENV = Collections.emptyMap();

    private MoreFileSystems ()
    {
        throw new Error("nice try!");
    }

    @Nonnull
    public static FileSystem openZip(final Path path, final boolean isReadOnly)
        throws IOException
    {
        Objects.requireNonNull(path);

        final URI zipURI = URI.create("jar:" + path.toUri());

        return FileSystems.newFileSystem(zipURI, ZIP_ENV);
    }

}
