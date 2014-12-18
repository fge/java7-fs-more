package com.github.fge.filesystem;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;

import static com.github.fge.filesystem.CustomAssertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MorePathsTest
{
    private Path path1;
    private Path path2;

    @BeforeMethod
    public void initMocks()
    {
        path1 = mock(Path.class);
        path2 = mock(Path.class);
    }

    @Test
    public void emptyPathToNormalizeIsReturnedAsIs()
    {
        when(path1.toString()).thenReturn("");
        final Path p = MorePaths.normalize(path1);
        assertThat(p).isSameAs(path1);
    }

    @Test
    public void nonEmptyPathToNormalizeCallsRegularNormalize()
    {
        when(path1.toString()).thenReturn("/foo");
        when(path1.normalize()).thenReturn(path2);
        final Path p = MorePaths.normalize(path1);
        assertThat(p).isSameAs(path2);
    }
}
