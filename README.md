## Read me first

This project is licensed under both LGPLv3 and ASL 2.0. See file LICENSE for more details.

## What this is

This is a collection of utilities for manipulating all things JSR 202, that is the new java.nio.file
API. It requires Java 7+.

## Contents

For now, pretty basic. Here is the full list:

* a .resolve() method which can resolve paths from different
  [providers](http://docs.oracle.com/javase/8/docs/api/java/nio/file/spi/FileSystemProvider.html);
* a .normalize() method which works around
  [bug 8037945](https://bugs.openjdk.java.net/browse/JDK-8037945).

It is in active development. What is currently being developed:

* a `chmod` like command to manipulate POSIX file permissions (single entry; recursive chmod will
  come later);
* a fail-fast recursive deletion.

## Contributions

Please submit ideas!

Other ideas are in the pipe. But if you would like a particular tool to be included, do not
hesitate!

