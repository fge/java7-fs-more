package com.github.fge.filesystem.posix;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
@ParametersAreNonnullByDefault
public final class PermissionsSet
{
    private final Set<PosixFilePermission> toAdd;
    private final Set<PosixFilePermission> toRemove;

    PermissionsSet(final Set<PosixFilePermission> toAdd,
        final Set<PosixFilePermission> toRemove)
    {
        this.toAdd = toAdd;
        this.toRemove = toRemove;
    }
}
