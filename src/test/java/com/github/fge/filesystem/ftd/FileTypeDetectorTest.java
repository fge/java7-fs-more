package com.github.fge.filesystem.ftd;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class FileTypeDetectorTest
{
    private FileSystem fs;
    private Path path;

    @BeforeMethod
    public void initfs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux().build("testfs");
        path = fs.getPath("/foo");
    }

    @DataProvider
    public Iterator<Object[]> samples()
    {
        final List<Object[]> list = new ArrayList<>();

        String resourcePath;
        String mimeType;

        resourcePath = "/ftd/sample.png";
        mimeType = "image/png";
        list.add(new Object[] { resourcePath, mimeType });

        return list.iterator();
    }

    @Test(dataProvider = "samples")
    public void fileTypeDetectionTest(final String resourcePath,
        final String mimeType)
        throws IOException
    {
        @SuppressWarnings("IOResourceOpenedButNotSafelyClosed")
        final InputStream in
            = FileTypeDetectorTest.class.getResourceAsStream(resourcePath);

        if (in == null)
            throw new IOException(resourcePath + " not found in classpath");

        try (
            final InputStream inref = in;
        ) {
            Files.copy(inref, path);
        }

        assertThat(Files.probeContentType(path)).isEqualTo(mimeType);
    }

    @AfterMethod
    public void closefs()
        throws IOException
    {
        fs.close();
    }
}
