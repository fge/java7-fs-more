## Read me first

This project is licensed under both LGPLv3 and ASL 2.0. See file LICENSE for more details.

## What this is

This is a collection of utilities for manipulating all things JSR 203, that is the new java.nio.file
API. It requires Java 7+.

The javadoc is [available online](https://fge.github.io/java7-fs-more). No version is released yet.

There are no prerequisites other than the JRE (7+).

## Contents

### Path resolution

The `MorePaths.resolve()` method is able to resolve paths issued from different filesystems, even if
their paths are incompatible.  For instance, if you have a Windows path `c:\foo\bar` and a Unix-like
path `hello/world`, calling:

```java
final Path resolved = MorePaths.resolve(windowsPath, unixPath);
```

will return path `c:\foo\bar\hello\world`.

See the javadoc for more details (limitations etc).

### Path normalization

Until recently, the JDK had a bug with empty path normalization under Unix (see
[here](https://bugs.openjdk.java.net/browse/JDK-8037945). This method works around this bug.

### Recursive copy

You can copy directories recursively, even across filesystems:

```java
final Path windowsPath = ...;
final Path unixPath = ...;

MoreFiles.copyRecursive(windowsPath, unixPath, RecursionMode.FAIL_FAST);
```

There are two modes: `FAIL_FAST` will fail at the first error encountered, whereas `KEEP_GOING` will
continue in the event of an error (but it will throw a `RecursiveCopyException`, which contains all
captured exceptions; see the javadoc).

Note that attributes (owner/group, permissions etc) are NOT copied.

### Recursive deletion

You can delete a path recursively:

```java
MoreFiles.deleteRecursively(victim, RecursionMode.KEEP_GOING);
```

### POSIX permission utility methods

For example:

```java
MoreFiles.setMode(path, 0644);
MoreFiles.setMode(path, "rw-r--r--");

MoreFiles.createFile(path, 0644);
MoreFiles.createFile(path, "rw-r--r--");

MoreFiles.createDirectory(path, 0750);
MoreFiles.createDirectory(path, "rwxr-x---");

MoreFiles.createDirectories(path, 0750);
MoreFiles.createDirectories(path, "rwxr-x---");
```

### `touch`

You also have `MoreFiles.touch()` which works in a similar manner than the classical Unix `touch`
command.

## Contributions

Please submit ideas!

Other ideas are in the pipe. But if you would like a particular tool to be included, do not
hesitate!

