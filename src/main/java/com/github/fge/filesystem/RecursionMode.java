package com.github.fge.filesystem;

import java.nio.file.CopyOption;
import java.nio.file.Path;

/**
 * Enumeration of options for recursive operations
 *
 * @see MoreFiles#copyRecursive(Path, Path, RecursionMode, CopyOption...)
 * @see MoreFiles#deleteRecursive(Path, RecursionMode)
 */
public enum RecursionMode
{
    /**
     * Fail at the first operation which fails
     */
    FAIL_FAST,
    /**
     * Try and keep going even if operation on one entry fails.
     */
    KEEP_GOING,
    ;
}
