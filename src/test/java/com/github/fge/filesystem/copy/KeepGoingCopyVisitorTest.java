package com.github.fge.filesystem.copy;

import com.github.fge.filesystem.exceptions.RecursiveOperationException;
import com.github.fge.filesystem.helpers.CustomSoftAssertions;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public final class KeepGoingCopyVisitorTest
{
    private FileSystem windowsFs;
    private FileSystem linuxFs;

    Path srcSuccess;
    Path dstSuccess;

    Path srcFailure;
    Path dstFailure;

    @BeforeClass
    public void initfs()
        throws IOException
    {
        windowsFs = Jimfs.newFileSystem(Configuration.windows());
        linuxFs = MemoryFileSystemBuilder.newLinux()
            .build("FailFastCopyVisitorTest");

        Path path;

        path = srcSuccess = windowsFs.getPath("c:\\success");
        Files.createDirectory(path);
        path = path.resolve("dir1");
        Files.createDirectory(path);
        path = path.resolve("file1");
        Files.createFile(path);
        path = path.resolveSibling("file2");
        Files.createFile(path);
        path = srcSuccess.resolve("dir2");
        Files.createDirectory(path);

        Files.createDirectory(dstSuccess = linuxFs.getPath("/success"));

        path = srcFailure = linuxFs.getPath("/failure");
        Files.createDirectory(path);
        path = path.resolve("dir1");
        Files.createDirectory(path);
        path = path.resolve("file1");
        Files.createFile(path);
        final Set<PosixFilePermission> perms
            = PosixFilePermissions.fromString("---------");
        Files.setPosixFilePermissions(srcFailure.resolve("dir1"), perms);

        Files.createDirectory(dstFailure = windowsFs.getPath("c:\\failure"));
    }

    @Test
    public void successfulRecursiveCopyCreatesAllDirsAndFiles()
        throws IOException
    {
        final RecursiveOperationException exception
            = new RecursiveOperationException();
        final FileVisitor<Path> visitor
            = new KeepGoingCopyVisitor(srcSuccess, dstSuccess, exception);

        Files.walkFileTree(srcSuccess, visitor);

        final CustomSoftAssertions soft = CustomSoftAssertions.create();

        Path path;

        path = dstSuccess.resolve("dir1");
        soft.assertThat(path).exists().isDirectory();
        path = path.resolve("file1");
        soft.assertThat(path).exists().isRegularFile();
        path = path.resolveSibling("file2");
        soft.assertThat(path).exists().isRegularFile();
        path = dstSuccess.resolve("dir2");
        soft.assertThat(path).exists().isDirectory();

        soft.assertAll();
    }

    @Test
    public void failureToCopyThrowsAppropriateException()
        throws IOException
    {
        final RecursiveOperationException exception
            = new RecursiveOperationException();
        final FileVisitor<Path> visitor
            = new KeepGoingCopyVisitor(srcFailure, dstFailure, exception);

        Files.walkFileTree(srcFailure, visitor);

        assertThat(exception.getSuppressed()).hasSize(1);
        assertThat(exception.getSuppressed()[0])
            .isExactlyInstanceOf(AccessDeniedException.class);
    }

    @AfterClass
    public void closefs()
        throws IOException
    {
        windowsFs.close();
        linuxFs.close();
    }
}