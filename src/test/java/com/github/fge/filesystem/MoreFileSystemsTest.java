package com.github.fge.filesystem;


import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public final class MoreFileSystemsTest {

    private FileSystem fs;
    private Path existingZip;
    private Path nonExistingZip;

    @BeforeMethod
    public void initFs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux().build("MoreFileSystemsTest");
        nonExistingZip = fs.getPath("/nonExisting.zip");
        existingZip = fs.getPath("/existing.zip");

        final URI uri = URI.create("jar:" + existingZip.toUri());
        final Map<String, ?> env = Collections.singletonMap("create", "true");

        try (
            final FileSystem tmp = FileSystems.newFileSystem(uri, env);
        ) {
        }
    }

    @AfterMethod
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}
