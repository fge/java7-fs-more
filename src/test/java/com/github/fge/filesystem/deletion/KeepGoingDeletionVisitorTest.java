package com.github.fge.filesystem.deletion;

import com.github.fge.filesystem.exceptions.RecursiveDeletionException;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import static org.assertj.core.api.Assertions.assertThat;

public final class KeepGoingDeletionVisitorTest
{
    private FileSystem fs;

    private Path root;
    private Path dir3;
    private RecursiveDeletionException exception;

    @BeforeClass
    public void initFileSystem()
        throws IOException
    {

        fs = MemoryFileSystemBuilder.newLinux().build(
            "KeepGoingDeletionVisitorTest");
        /*
         * Create three directory and remove the write access on the second
         * directory
         */
        root = fs.getPath("/dir1");
        final Path dir2 = root.resolve("dir2");
        dir3 = root.resolve("dir3");

        Files.createDirectories(dir2);
        Files.createDirectory(dir3);

        /**
         * Create file1 inside root/
         *
         * Create file2 inside root/dir2/
         */

        final Path file1 = root.resolve("file1");
        final Path file2 = dir2.resolve("file2");
        Files.createFile(file1);
        Files.createFile(file2);
        Files.setPosixFilePermissions(dir2,
            PosixFilePermissions.fromString("r-xr-xr-x"));
    }

    @BeforeMethod
    public void initException()
    {
        exception = new RecursiveDeletionException();
    }

    @Test
    public void recursiveDeletionFailureSuppressesExceptions()
        throws IOException
    {
        final FileVisitor<Path> visitor
            = new KeepGoingDeletionVisitor(root, exception);

        Files.walkFileTree(root, visitor);

        assertThat(exception.getSuppressed()).isNotEmpty();
    }

    @Test
    public void recursiveDeleteWorks()
        throws IOException
    {
        final FileVisitor<Path> visitor
            = new KeepGoingDeletionVisitor(dir3, exception);

        final Path dir4 = dir3.resolve("dir4");
        final Path dir5 = dir4.resolve("dir5");

        Files.walkFileTree(dir3, visitor);

        try (
            final AutoCloseableSoftAssertions soft
                = new AutoCloseableSoftAssertions();
        ) {
            /**
             * All the directories should be deleted
             */
            soft.assertThat(dir3).doesNotExist();
            soft.assertThat(dir4).doesNotExist();
            soft.assertThat(dir5).doesNotExist();
        }
    }

    @AfterClass
    public void destroyFileSystem()
        throws IOException
    {
        fs.close();
    }
}
