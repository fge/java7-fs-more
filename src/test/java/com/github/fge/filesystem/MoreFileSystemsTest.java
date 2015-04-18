package com.github.fge.filesystem;


import com.github.fge.filesystem.readonly.ReadOnlyFileSystem;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MoreFileSystemsTest {

    private FileSystem fs;
    private Path existing;
    private Path nonExisting;

    @BeforeMethod
    public void initFs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux().build("MoreFileSystemsTest");
        nonExisting = fs.getPath("/nonExisting.zip");
        existing = fs.getPath("/existing.zip");

        final URI uri = URI.create("jar:" + existing.toUri());
        final Map<String, ?> env = Collections.singletonMap("create", "true");

        try (
            final FileSystem tmp = FileSystems.newFileSystem(uri, env);
        ) {
        }
    }

    @Test
    public void openZipReadWriteTest()
        throws IOException
    {
        try (
            final FileSystem fs = MoreFileSystems.openZip(existing, false);
        ) {
            assertThat(fs).isNotInstanceOf(ReadOnlyFileSystem.class);
        }
    }

    @Test
    public void openZipReadOnlyTest()
        throws IOException
    {
        try (
            final FileSystem fs = MoreFileSystems.openZip(existing, true);
        ) {
            assertThat(fs).isInstanceOf(ReadOnlyFileSystem.class);
        }
    }

    @Test
    public void createZipNonExistingTest()
        throws IOException
    {
        try (
            final FileSystem fs = MoreFileSystems.createZip(nonExisting);
        ) {
            // nothing
        }

        assertThat(nonExisting).exists();
    }

    @Test
    public void createZipAlreadyExistsTest()
        throws IOException
    {
        try {
            MoreFileSystems.createZip(existing);
            shouldHaveThrown(FileAlreadyExistsException.class);
        } catch (FileAlreadyExistsException ignored) {
        }
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void readOnlyFromReadWriteFsTest()
    {
        final FileSystem fs2 = mock(FileSystem.class);

        // This is the default but let's be explicit
        when(fs2.isReadOnly()).thenReturn(false);

        final FileSystem rofs = MoreFileSystems.readOnly(fs2);

        assertThat(rofs).isNotSameAs(fs2)
            .isInstanceOf(ReadOnlyFileSystem.class);
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void readOnlyFromReadOnlyFsTest()
    {
        final FileSystem fs2 = mock(FileSystem.class);

        when(fs2.isReadOnly()).thenReturn(true);

        final FileSystem rofs = MoreFileSystems.readOnly(fs2);

        assertThat(rofs).isSameAs(fs2);
    }

    @AfterMethod
    public void closeFs()
        throws IOException
    {
        fs.close();
    }
}
