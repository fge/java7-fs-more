package com.github.fge.filesystem.deletion;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;

import com.github.fge.filesystem.exceptions.RecursiveDeletionException;

public class KeepGoingDeletionVisitor implements FileVisitor<Path> {
	private final FileSystemProvider provider;
	private final RecursiveDeletionException exception;

	public KeepGoingDeletionVisitor(final Path victim, RecursiveDeletionException exception) {
		provider = Objects.requireNonNull(victim).getFileSystem().provider();
		this.exception = exception;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		try {
			provider.delete(file);
		} catch (IOException ioException) {
			exception.addSuppressed(ioException);
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		exception.addSuppressed(exc);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		try {
			if(null==exc) {
				provider.delete(dir);
			}
			else {
				exception.addSuppressed(exc);
			}
		} catch (IOException ioException) {
			exception.addSuppressed(ioException);
		}
		return FileVisitResult.CONTINUE;
	}

}
