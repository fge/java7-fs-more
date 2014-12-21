package com.github.fge.filesystem;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static com.github.fge.filesystem.helpers.CustomAssertions.assertThat;
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
    
    @Test
    public void setTimeToFileTest() throws IOException {
    	FileTime fileTime = FileTime.fromMillis(System.currentTimeMillis());
    	final Path path = MorePaths.setTimes(path1, fileTime);
    	assertThat(path).exists().hasAccessTime(fileTime).hasModificationTime(fileTime);
    }
}
