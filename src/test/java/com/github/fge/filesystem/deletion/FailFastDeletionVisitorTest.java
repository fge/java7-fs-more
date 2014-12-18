package com.github.fge.filesystem.deletion;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;

public final class FailFastDeletionVisitorTest
{
    private FileSystem fs;

    private Path foo;

    @BeforeClass
    public void initFileSystem()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux().build("foo");
        /*
         * Create one directory and one file; remove the write access on the
         * directory
         */
        foo = fs.getPath("/foo");
        Files.createDirectory(foo);
        Files.createFile(foo.resolve("bar"));
        Files.setPosixFilePermissions(foo,
            PosixFilePermissions.fromString("r-xr-xr-x"));
    }

    @Test
    public void nonWritableDirectoryThrowsAccessDeniedException()
    {
        final FileVisitor<Path> visitor = new FailFastDeletionVisitor(foo);
        try {
            Files.walkFileTree(foo, visitor);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(AccessDeniedException.class);
        }
    }

    @AfterClass
    public void destroyFileSystem()
        throws IOException
    {
        fs.close();
    }
}