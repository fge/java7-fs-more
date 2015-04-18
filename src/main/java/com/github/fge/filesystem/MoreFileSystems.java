package com.github.fge.filesystem;

import com.github.fge.filesystem.exceptions.IsDirectoryException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import javax.annotation.*;

/**
 * Utility class to complement JDK's {@link Files}
 * <p/>
 * <p>Unless otherwise noted, all methods in this class do not accept null
 * arguments and will throw a {@link NullPointerException} if a null argument
 * is passed to them.</p>
 */
@ParametersAreNonnullByDefault
public final class MoreFileSystems {

    private MoreFileSystems ()
    {
        throw new Error("nice try!");
    }

    @Nonnull
    public static FileSystem openZip (Path path, boolean isReadOnly) throws IOException {

        Objects.requireNonNull(path);

        final URI zipURI = URI.create("jar:" + path.toUri().toString());
        final Map<String, String> env
                = Collections.singletonMap("readonly", String.valueOf(isReadOnly));

        return FileSystems.newFileSystem(zipURI, env);
    }

}
