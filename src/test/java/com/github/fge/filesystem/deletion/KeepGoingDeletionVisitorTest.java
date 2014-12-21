package com.github.fge.filesystem.deletion;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.fge.filesystem.exceptions.RecursiveDeletionException;
import com.github.fge.filesystem.helpers.CustomSoftAssertions;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;

public final class KeepGoingDeletionVisitorTest {
	private FileSystem fs;

	private Path root;
	private Path dir2;
	private Path dir3;
	private Path file1;
	private Path file2;

	@BeforeClass
	public void initFileSystem() throws IOException {

		fs = MemoryFileSystemBuilder.newLinux().build(
				"KeepGoingDeletionVisitorTest");
		/*
		 * Create three directory and remove the write access on the second
		 * directory
		 */
		root = fs.getPath("/dir1");
		dir2 = root.resolve("dir2");
		dir3 = root.resolve("dir3");

		Files.createDirectories(dir2);
		Files.createDirectory(dir3);
		Files.setPosixFilePermissions(dir2,
				PosixFilePermissions.fromString("r-xr-xr-x"));

		/**
		 * Create file1 inside root/
		 * 
		 * Create file2 inside root/dir2/
		 */

		file1 = root.resolve("file1");
		file2 = dir2.resolve("file2");
		Files.createFile(file1);
		Files.createFile(file2);
	}

	@Test
	public void recursiveDeletionFailureSuppressesExceptions () throws IOException {
		RecursiveDeletionException exception = new RecursiveDeletionException();
		final FileVisitor<Path> visitor = new KeepGoingDeletionVisitor(root, exception);

		Files.walkFileTree(root, visitor);
		assertThat(exception.getSuppressed()).hasSize(1);
		assertThat(exception.getSuppressed()[0])
        .isExactlyInstanceOf(AccessDeniedException.class);
	}

	@Test
	public void recursiveDeleteWorks() throws IOException {
		RecursiveDeletionException exception = new RecursiveDeletionException();
		final FileVisitor<Path> visitor = new KeepGoingDeletionVisitor(dir3, exception);
		Path dir4 = dir3.resolve("dir4");
		Path dir5 = dir4.resolve("dir5");

		Files.walkFileTree(dir3, visitor);

		final CustomSoftAssertions soft = CustomSoftAssertions.create();
		/**
		 * All the directories should be deleted
		 */
		soft.assertThat(dir3).doesNotExist();
		soft.assertThat(dir4).doesNotExist();
		soft.assertThat(dir5).doesNotExist();

		soft.assertAll();
	}

	@AfterClass
	public void destroyFileSystem() throws IOException {
		fs.close();
	}
}