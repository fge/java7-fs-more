package com.github.fge.filesystem.helpers;

import org.assertj.core.api.SoftAssertions;

import java.nio.file.Path;

public final class CustomSoftAssertions
    extends SoftAssertions
{
    public static CustomSoftAssertions create()
    {
        return new CustomSoftAssertions();
    }

    private CustomSoftAssertions()
    {
    }

    public PathAssert assertThat(final Path path)
    {
        return proxy(PathAssert.class, Path.class, path);
    }
}
