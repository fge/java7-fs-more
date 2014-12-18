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

import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;

public final class FailFastDeletionVisitorTest
{
    private FileSystem fs;

    private Path nonWritable;
    private Path withSymlink;
    private Path symlink;
    private Path symlinkTarget;
    private Path fileToDelete;

    @BeforeClass
    public void initFileSystem()
        throws IOException
    {
        Path path;

        fs = MemoryFileSystemBuilder.newLinux().build("testfs");
        /*
         * Create one directory and one file; remove the write access on the
         * directory
         */
        nonWritable = fs.getPath("/nonWritable");
        Files.createDirectory(nonWritable);
        Files.createFile(nonWritable.resolve("bar"));
        Files.setPosixFilePermissions(nonWritable,
            PosixFilePermissions.fromString("r-xr-xr-x"));
        /*
         * Create one file at the top level; create one directory, and in this
         * directory, create a symlink to the file at the top level.
         */
        symlinkTarget = fs.getPath("/target");
        Files.createFile(symlinkTarget);
        withSymlink = fs.getPath("/withSymlink");
        symlink = withSymlink.resolve("symlink");
        Files.createDirectory(withSymlink);
        Files.createSymbolicLink(symlink, symlinkTarget);
        /*
         * Create a single file
         */
        fileToDelete = fs.getPath("/whatever");
        Files.createFile(fileToDelete);
    }

    @Test
    public void nonWritableDirectoryThrowsAccessDeniedException()
    {
        final FileVisitor<Path> visitor
            = new FailFastDeletionVisitor(nonWritable);

        try {
            Files.walkFileTree(nonWritable, visitor);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(AccessDeniedException.class);
        }
    }

    @Test
    public void failFastDeletionWillNotFollowSymlinks()
        throws IOException
    {
        final FileVisitor<Path> visitor
            = new FailFastDeletionVisitor(withSymlink);

        Files.walkFileTree(withSymlink, visitor);

        assertThat(Files.exists(symlinkTarget)).isTrue();
        assertThat(Files.notExists(symlink)).isTrue();
        assertThat(Files.notExists(withSymlink)).isTrue();
    }

    @Test
    public void removingSingleFileWorks()
        throws IOException
    {
        final FileVisitor<Path> visitor
            = new FailFastDeletionVisitor(fileToDelete);

        Files.walkFileTree(fileToDelete, visitor);

        assertThat(Files.notExists(fileToDelete));
    }

    @AfterClass
    public void destroyFileSystem()
        throws IOException
    {
        fs.close();
    }
}