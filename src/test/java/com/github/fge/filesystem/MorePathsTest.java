package com.github.fge.filesystem;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public final class MorePathsTest
{
	private Path path1;
	private Path path2;
	private FileSystem fs1;
	private FileSystem fs2;
	private FileSystemProvider provider;
	private final Path path3 = mock(Path.class); 

	@BeforeMethod
	public void initMocks()
	{
		path1 = mock(Path.class);
		path2 = mock(Path.class);
		fs1 = mock(FileSystem.class);
		fs2 = mock(FileSystem.class);
		provider = mock(FileSystemProvider.class);
	}

	@Test
	public void resolvingPathWithSameFileSystemCallsFirstPathResolve()
	{
		when(path1.getFileSystem()).thenReturn(fs1);
		when(path2.getFileSystem()).thenReturn(fs1);

		MorePaths.resolve(path1, path2);

		verify(path1).resolve(same(path2));
	}
	
	@Test
	public void resolvingPathWithSameFileSystemProvider()
	{
		
		final String nameElement = "test";
		
		when(path1.getFileSystem()).thenReturn(fs1);
		when(path2.getFileSystem()).thenReturn(fs2);
		
		when(fs1.provider()).thenReturn(provider);
		when(fs2.provider()).thenReturn(provider);

		when(path2.toString()).thenReturn(nameElement);
		when(fs1.getPath(same(nameElement))).thenReturn(path3); 
		
		MorePaths.resolve(path1, path2);
		
		verify(path1).resolve(same(path3));
	}
} 
