package com.github.fge.filesystem.ftd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Arrays;

public final class PngFileTypeDetector
    extends FileTypeDetector
{
    private static final byte[] PNG_HEADER = {
        (byte) 0x89,
        (byte) 0x50, (byte) 0x4E, (byte) 0x47,
        (byte) 0x0D, (byte) 0x0A,
        (byte) 0x1A,
        (byte) 0x0A
    };

    private static final int PNG_HEADER_SIZE = PNG_HEADER.length;

    @Override
    public String probeContentType(final Path path)
        throws IOException
    {
        final byte[] buf = new byte[PNG_HEADER_SIZE];

        try (
            final InputStream in = Files.newInputStream(path);
        ) {
            if (in.read(buf) != PNG_HEADER_SIZE)
                return null;
        }

        return Arrays.equals(buf, PNG_HEADER) ? "image/png" : null;
    }
}
