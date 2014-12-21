package com.github.fge.filesystem;

/**
 * Enumeration of options for recursive operations
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
