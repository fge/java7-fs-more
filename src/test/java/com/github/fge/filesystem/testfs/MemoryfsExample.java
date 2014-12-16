package com.github.fge.filesystem.testfs;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public final class MemoryfsExample
{
    private static final Set<PosixFilePermission> NOWRITE
        = PosixFilePermissions.fromString("r-xr--r--");


    public static void main(final String... args)
        throws IOException
    {
        try (
            final FileSystem fs = MemoryFileSystemBuilder.newLinux()
                .build("x");
        ) {
            playWith(fs);
        }
    }

    private static void playWith(final FileSystem fs)
        throws IOException
    {
        final Path dir = fs.getPath("/foo");
        Files.createDirectory(dir);
        final Path file = dir.resolve("bar");
        try (
            final OutputStream out = Files.newOutputStream(file);
        ) {
        }

        final Set<PosixFilePermission> perms
            = PosixFilePermissions.fromString("r-x------");
        Files.setPosixFilePermissions(dir, perms);
        Files.delete(file);
    }
}
