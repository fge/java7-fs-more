package com.github.fge.filesystem;


import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.*;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import static com.github.fge.filesystem.helpers.CustomAssertions.assertThat;
import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;

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

    @Test
    public void openZipFileSystemOpensFileAsReadOnly()
            throws IOException
    {

        final String fileName = "/zipFile.zip";
        try (
                final FileSystem fileSystem = MoreFileSystems.openZip(existingZip, true);
        ) {
            Path p = fileSystem.getPath(fileName);
            Files.createFile(p);
            shouldHaveThrown(ReadOnlyFileSystemException.class);
        } catch (ReadOnlyFileSystemException e) {
            assertThat(e)
                    .isExactlyInstanceOf(ReadOnlyFileSystemException.class);
        }
    }


    @AfterMethod
    public void closeFs()
            throws IOException
    {
        fs.close();
    }

}
