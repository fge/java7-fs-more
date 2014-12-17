package com.github.fge.filesystem;

import org.assertj.core.api.ObjectAssert;

import java.nio.file.Path;

public final class PathAssert
    extends ObjectAssert<Path>
{
    public PathAssert(final Path actual)
    {
        super(actual);
    }
}
