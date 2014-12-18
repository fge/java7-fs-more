package com.github.fge.filesystem;

import com.github.fge.filesystem.exception.UnresolvablePathException;
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static com.github.fge.filesystem.CustomAssertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public void path2WithSameFileSystemCallsPath1Resolve()
    {
        when(path1.getFileSystem()).thenReturn(fs1);
        when(path2.getFileSystem()).thenReturn(fs1);

        MorePaths.resolve(path1, path2);

        verify(path1).resolve(same(path2));
    }

    @Test
    public void path2OnSameProviderAndDifferentFsCallsPath1StringResolve()
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
    public void cannotResolveAbsolutePath2WithNoRoot()
    {
        //noinspection AutoBoxing
        when(path2.isAbsolute()).thenReturn(true);

        // this is the default and that is what we need
        //when(path2.getRoot()).thenReturn(null);

        when(path1.getFileSystem()).thenReturn(fs1);
        when(path2.getFileSystem()).thenReturn(fs2);

        when(fs1.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        try {
            MorePaths.resolve(path1, path2);
            shouldHaveThrown(UnresolvablePathException.class);
        } catch (UnresolvablePathException e) {
            assertThat(e)
                .isExactlyInstanceOf(UnresolvablePathException.class)
                .hasMessage("path to resolve is absolute but has no root");
        }
    }

    @Test
    public void cannotResolveIfPath2HasNoCommonRootWithPath1()
    {
        final String root1 = "foo";
        final String root2 = "bar";

        final Path path1Root = mock(Path.class);

        when(path1Root.toString()).thenReturn(root1);
        when (fs1.getRootDirectories())
            .thenReturn(Collections.singleton(path1Root));

        final Path path2Root = mock(Path.class);

        when(path2Root.toString()).thenReturn(root2);
        when(path2.getRoot()).thenReturn(path2Root);

        //noinspection AutoBoxing
        when(path2.isAbsolute()).thenReturn(true);

        //this is default and that is what we need
        //when(path2.getRoot()).thenReturn(null);

        when(path1.getFileSystem()).thenReturn(fs1);
        when(path2.getFileSystem()).thenReturn(fs2);

        when(fs1.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        try {
            MorePaths.resolve(path1, path2);
            shouldHaveThrown(UnresolvablePathException.class);
        } catch (UnresolvablePathException e) {
            assertThat(e)
                .isExactlyInstanceOf(UnresolvablePathException.class)
                .hasMessage("root of path to resolve is incompatible "
                    + "with source path");
        }
    }

    @Test
    public void absolutePath2WillStillResolveToPathOnPath1FileSystem()
        throws IOException
    {
        try (
            final FileSystem fsOne
                = MemoryFileSystemBuilder.newLinux().build("x");
            final FileSystem fsTwo
                = Jimfs.newFileSystem(Configuration.unix());
        ) {
            final Path pathOne = fsOne.getPath("/foo");
            final Path pathTwo = fsTwo.getPath("/bar");

            final Path result = MorePaths.resolve(pathOne, pathTwo);

            assertThat(result.getFileSystem()).isSameAs(fsOne);
            assertThat(result.toString()).isEqualTo("/bar");
        }
    }

    @Test
    public void cannotResolveNonAbsolutePath2WhichHasARoot()
    {
        when(path1.getFileSystem()).thenReturn(fs1);
        when(path2.getFileSystem()).thenReturn(fs2);

        when(fs1.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        //noinspection AutoBoxing
        when(path2.isAbsolute()).thenReturn(false);
        when(path2.getRoot()).thenReturn(path2);

        try {
            MorePaths.resolve(path1, path2);
            shouldHaveThrown(UnresolvablePathException.class);
        } catch (UnresolvablePathException e) {
            assertThat(e).isExactlyInstanceOf(UnresolvablePathException.class)
                .hasMessage("path to resolve is not absolute but has a root");
        }

    }

    @Test
    public void emptyPath2JustReturnsPath1()
    {
        when(path1.getFileSystem()).thenReturn(fs1);
        when(path2.getFileSystem()).thenReturn(fs2);

        when(fs1.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        //noinspection AutoBoxing
        when(path2.isAbsolute()).thenReturn(false);
        when(path2.toString()).thenReturn("");

        assertThat(MorePaths.resolve(path1, path2)).isSameAs(path1);
    }

    @Test
    public void fullResolutionWorksEvenWithDifferentPathModels()
        throws IOException
    {
        try (
            final FileSystem fsOne
                = MemoryFileSystemBuilder.newLinux().build("x");
            final FileSystem fsTwo
                = Jimfs.newFileSystem(Configuration.windows());
        ) {
            //Shadowing here
            final Path fsOnePath = fsOne.getPath("/foo/bar");
            final Path fsTwoPath = fsTwo.getPath("toto\\le\\heros");

            final String expected = "/foo/bar/toto/le/heros";

            assertThat(MorePaths.resolve(fsOnePath, fsTwoPath).toString())
                .isEqualTo(expected);
        }
    }
}