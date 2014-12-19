package com.github.fge.filesystem.helpers;

import org.assertj.core.api.ObjectAssert;

import java.nio.file.Files;
import java.nio.file.Path;

public class PathAssert
    extends ObjectAssert<Path>
{
    public PathAssert(final Path actual)
    {
        super(actual);
    }

    public final PathAssert doesNotExist()
    {
        if (Files.exists(actual))
            failWithMessage("expected " + actual + " not to exist, but it has "
                + "been found");
        return this;
    }

    public final PathAssert exists()
    {
        if (Files.notExists(actual))
            failWithMessage("expected " + actual + " to exist, but it has not "
                + "been found");
        return this;
    }
}
