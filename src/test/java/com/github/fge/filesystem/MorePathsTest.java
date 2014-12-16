package com.github.fge.filesystem;

import com.github.fge.filesystem.exception.UnresolvablePathException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public final class MorePathsTest
{
    private Path path1;
    private Path path2;
    private Path path3;
    private FileSystem fs1;
    private FileSystem fs2;
    private FileSystemProvider provider1;
    private FileSystemProvider provider2;

    @BeforeMethod
    public void initMocks()
    {
        path1 = mock(Path.class);
        path2 = mock(Path.class);
        path3 = mock(Path.class);
        fs1 = mock(FileSystem.class);
        fs2 = mock(FileSystem.class);
        provider1 = mock(FileSystemProvider.class);
        provider2 = mock(FileSystemProvider.class);
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

        when(fs1.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider1);

        when(path2.toString()).thenReturn(nameElement);
        when(fs1.getPath(same(nameElement))).thenReturn(path3);

        MorePaths.resolve(path1, path2);

        verify(path1).resolve(same(path3));
    }

    @Test
    public void targetAbsolutePathWithNoRootShouldFailAppropriately()
    {

        when(path2.isAbsolute()).thenReturn(true);

        //this is default and that is what we need
        //when(path2.getRoot()).thenReturn(null);

        when(path1.getFileSystem()).thenReturn(fs1);
        when(path2.getFileSystem()).thenReturn(fs2);

        when(fs1.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        try {
            MorePaths.resolve(path1, path2);

            failBecauseExceptionWasNotThrown(
                    UnresolvablePathException.class
            );

        } catch (UnresolvablePathException e) {
            assertThat(e)
                    .isExactlyInstanceOf(UnresolvablePathException.class)
                    .hasMessage("path to resolve is absolute but has no root");
        }

    }

    @Test
    public void noneOfTheRootPathsOfPath1MatchesPath2Root()
    {
        final String root1 = "foo";
        final String root2 = "bar";

        final Path path1Root = mock(Path.class);

        when(path1Root.toString()).thenReturn(root1);
        when (fs1.getRootDirectories()).thenReturn(Collections.singleton(path1Root));

        final Path path2Root = mock(Path.class);

        when(path2Root.toString()).thenReturn(root2);
        when(path2.getRoot()).thenReturn(path2Root);

        when(path2.isAbsolute()).thenReturn(true);

        //this is default and that is what we need
        //when(path2.getRoot()).thenReturn(null);

        when(path1.getFileSystem()).thenReturn(fs1);
        when(path2.getFileSystem()).thenReturn(fs2);

        when(fs1.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        try {
            MorePaths.resolve(path1, path2);

            shouldHaveThrown(
                    UnresolvablePathException.class
            );

        } catch (UnresolvablePathException e) {
            assertThat(e)
                    .isExactlyInstanceOf(UnresolvablePathException.class)
                    .hasMessage("root of path to resolve is incompatible with source path");
        }

    }

}