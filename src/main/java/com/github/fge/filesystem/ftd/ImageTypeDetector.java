package com.github.fge.filesystem.ftd;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Iterator;

public class ImageTypeDetector
    extends FileTypeDetector
{
    private final String IMAGE = "image/";

    @Override
    public String probeContentType(final Path path)
        throws IOException
    {
        try (
            final ImageInputStream in = ImageIO.createImageInputStream(
                Files.newInputStream(path));
        ) {
            final Iterator<ImageReader> iter = ImageIO.getImageReaders(
                in);
            if (!iter.hasNext()) {
                return null;
            }
            final ImageReader reader = iter.next();
            return IMAGE + reader.getFormatName();
        }

    }
}
