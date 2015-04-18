package com.github.fge.filesystem.ftd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.zip.ZipInputStream;

public final class ZipFileTypeDetector
    extends FileTypeDetector
{
    @Override
    public String probeContentType(final Path path)
        throws IOException
    {
        // Rely on what the JDK has to offer...
        try (
            final InputStream in = Files.newInputStream(path);
            final ZipInputStream z = new ZipInputStream(in);
        ) {
            return "application/zip";
        }
    }
}
