## Read me first

This project is licensed under both LGPLv3 and ASL 2.0. See file LICENSE for more details.

## What this is

This is a collection of utilities for manipulating all things JSR 203, that is the new java.nio.file
API. It requires Java 7+.

There are no dependencies other than the JRE.

## Versions

The current version is **0.2.0**. It is available on Maven and
[Bintray](https://bintray.com/fge/maven/java7-fs-more/view).

The javadoc is [available online](https://fge.github.io/java7-fs-more).

See also file `CONTRIBUTORS.md`.

## Contents

### Recursive copy and deletion

You can copy directories recursively, even across filesystems, and delete recursively:

```java
final Path windowsPath = ...;
final Path unixPath = ...;

MoreFiles.copyRecursive(windowsPath, unixPath, RecursionMode.FAIL_FAST);

final Path victim = ...;
MoreFiles.deleteRecursively(victim, RecursionMode.KEEP_GOING);
```

Recursive operations have two modes: `FAIL_FAST` will fail at the first error encountered, whereas
`KEEP_GOING` will continue in the event of an error; but in the event of an error, an exception
_will_ be thrown, which "embeds" all errors it has encountered during the operation.

See the javadoc for more details.

### Zips as filesystems; read only filesystems

This package provides convenience methods to manipulate zips as filesystems using the [zip
filesystem
provider](http://docs.oracle.com/javase/8/docs/technotes/guides/io/fsp/zipfilesystemprovider.html):

* `MoreFileSystems.createZip()` will create a new zip file;
* `MoreFileSystems.openZip()` will open an existing zip file, either read write or read only.

There is also `MoreFileSystems.readOnly()` which will return a read only version of an existing
filesystem.

### POSIX permission utility methods

You have a lot of utility methods to set or change POSIX permissions on
files/directories:

```java
// Set the permissions on a file or directory
MoreFiles.setMode(path, 0644);
MoreFiles.setMode(path, "rw-r--r--");

// Alter the permissions of a file or directory
MoreFiles.changeMode(path, "o-rwx,g+w");
```

There are also "umask insensitive" versions of `create{File,Directory,Directories}`:

```java
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

## Contributions

Please submit ideas!

Other ideas are in the pipe. But if you would like a particular tool to be included, do not
hesitate!

