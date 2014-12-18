package com.github.fge.filesystem;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import static com.github.fge.filesystem.CustomAssertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MorePathsTest {

    private Path path1;
    private Path path2;

    @BeforeMethod
    public void initMocks()
    {
        path1 = mock(Path.class);
        path2 = mock(Path.class);
    }

    @Test
    public void emptyPathNormalizeTest() {
        when(path1.toString()).thenReturn("");
        Path p = MorePaths.normalize(path1);
        assertThat(p).isSameAs(path1);
    }

    @Test
    public void pathNormalizeTest() {
        when(path1.toString()).thenReturn("/foo");
        when(path1.normalize()).thenReturn(path2);
        Path p = MorePaths.normalize(path1);
        assertThat(p).isSameAs(path2);
    }

}
