package com.github.fge.filesystem;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import static com.github.fge.filesystem.helpers.CustomAssertions.assertThat;
import static com.github.fge.filesystem.helpers.CustomAssertions.shouldHaveThrown;

public final class MoreFilesRecursiveCopyTest
{
    private FileSystem fs;

    private Path srcDir;
    private Path nonEmptyDestination;
    private Path symlinkSrc;
    private Path nonExistingDestination;
    private Path destination1;
    private Path destination2;

    @BeforeClass
    public void initfs()
        throws IOException
    {
        fs = MemoryFileSystemBuilder.newLinux()
            .build("MoreFilesRecursiveCopyTest");

        srcDir = fs.getPath("/srcDir");
        Files.createDirectory(srcDir);
        Files.createFile(srcDir.resolve("file1"));
        Files.createFile(srcDir.resolve("file2"));

        nonEmptyDestination = fs.getPath("/nonEmptyDestination");
        Files.createDirectory(nonEmptyDestination);
        Files.createFile(nonEmptyDestination.resolve("file"));

        symlinkSrc = fs.getPath("/symlinkSrc");
        Files.createSymbolicLink(symlinkSrc, srcDir);

        nonExistingDestination = fs.getPath("/nonExistingDestination");
        destination1 = Files.createFile(fs.getPath("/destination1"));
        destination2 = Files.createDirectory(fs.getPath("/destination2"));
    }

    @Test
    public void nonExistingSourceThrowsNoSuchFileException()
    {
        final Path nonexisting = fs.getPath("/nonexisting");

        try {
            MoreFiles.copyRecursive(nonexisting, nonexisting,
                RecursionMode.FAIL_FAST);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(NoSuchFileException.class);
        }
    }

    @Test
    public void mustFailIfDstExistsAndReplaceExistingNotSpecified()
    {
        try {
            MoreFiles.copyRecursive(srcDir, nonEmptyDestination,
                RecursionMode.FAIL_FAST);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(FileAlreadyExistsException.class);
        }
    }

    @Test
    public void willNotReplaceNonEmptyDestination()
    {
        try {
            MoreFiles.copyRecursive(srcDir, nonEmptyDestination,
                RecursionMode.FAIL_FAST, StandardCopyOption.REPLACE_EXISTING);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isExactlyInstanceOf(DirectoryNotEmptyException.class);
        }
    }

    @Test
    public void willDeleteExistingFileBeforeCopying()
        throws IOException
    {
        MoreFiles.copyRecursive(srcDir, destination1, RecursionMode.FAIL_FAST,
            StandardCopyOption.REPLACE_EXISTING);

        final Path path = destination1;

        assertThat(path).exists().isDirectory();
        assertThat(path.resolve("file1")).exists().isRegularFile();
        assertThat(path.resolve("file2")).exists().isRegularFile();
    }

    @Test
    public void willDeleteExistingEmptyDirectoryBeforeCopying()
        throws IOException
    {
        MoreFiles.copyRecursive(srcDir, destination2, RecursionMode.FAIL_FAST,
            StandardCopyOption.REPLACE_EXISTING);

        final Path path = destination2;

        assertThat(path).exists().isDirectory();
        assertThat(path.resolve("file1")).exists().isRegularFile();
        assertThat(path.resolve("file2")).exists().isRegularFile();
    }

    @Test
    public void willAlsoCopySimpleFile()
        throws IOException
    {
        final byte[] content = new byte[128];
        new Random().nextBytes(content);

        final Path src = srcDir.resolve("file1");
        Files.write(src, content);
        final Path dst = fs.getPath("/dstfile");

        MoreFiles.copyRecursive(src, dst, RecursionMode.FAIL_FAST);

        assertThat(dst).exists().isRegularFile();

        final byte[] copied = Files.readAllBytes(dst);

        assertThat(copied).isEqualTo(content);
    }

    @AfterClass
    public void closefs()
        throws IOException
    {
        fs.close();
    }
}
