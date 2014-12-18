package com.github.fge.filesystem.deletion;

/**
 * Enumeration of options for recursive deletion
 */
public enum DeleteRecursiveOption
{
    /**
     * Fail at the first filesystem entry (file, directory or other) which could
     * not be deleted
     */
    FAIL_FAST,
    /**
     * Try and keep going deleting entries even if one entry fails.
     *
     * <p>TODO: define exact reporting mechanism</p>
     */
    KEEP_GOING,
    ;
}
