package com.github.fge.filesystem.deletion;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FailFastDeletionVisitor implements FileVisitor<Path> {
	
	private Path victim;
	private final FileSystemProvider provider;
	
	public FailFastDeletionVisitor(final Path victim) {
		this.victim = Objects.requireNonNull(victim);
		this.provider = victim.getFileSystem().provider();
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
