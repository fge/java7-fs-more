package com.github.fge.filesystem;

import com.github.fge.filesystem.copy.FailFastCopyVisitor;
import com.github.fge.filesystem.copy.KeepGoingCopyVisitor;
import com.github.fge.filesystem.deletion.FailFastDeletionVisitor;
import com.github.fge.filesystem.deletion.KeepGoingDeletionVisitor;
import com.github.fge.filesystem.exceptions.InvalidIntModeException;
import com.github.fge.filesystem.exceptions.IsDirectoryException;
import com.github.fge.filesystem.exceptions.RecursiveCopyException;
import com.github.fge.filesystem.exceptions.RecursiveDeletionException;
import com.github.fge.filesystem.posix.ModeParser;
import com.github.fge.filesystem.posix.PermissionsSet;
import com.github.fge.filesystem.posix.PosixModes;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * Utility class to complement JDK's {@link Files}
 * <p/>
 * <p>Unless otherwise noted, all methods in this class do not accept null
 * arguments and will throw a {@link NullPointerException} if a null argument
 * is passed to them.</p>
 */
@ParametersAreNonnullByDefault
public final class MoreFiles
{

    //Right now we are supporting only these options.
    private static final List<OpenOption> VALID_OPTIONS = Arrays
        .<OpenOption>asList(StandardOpenOption.CREATE,
            StandardOpenOption.CREATE_NEW, LinkOption.NOFOLLOW_LINKS);

    private MoreFiles()
    {
        throw new Error("nice try!");
    }

    /**
     * Recursively copy a source to a destination
     * <p/>
     * <p>This command will work even across filesystems.</p>
     * <p/>
     * <p>Note that this command only supports directories and files. If the
     * source is a symbolic link, though, it will be followed (see {@link
     * Path#toRealPath(LinkOption...)}). Any symbolic link encountered during
     * a copy will trigger an {@link UnsupportedOperationException}.</p>
     * <p/>
     * <p>There are two recursion modes: {@link RecursionMode#FAIL_FAST fail
     * fast} and {@link RecursionMode#KEEP_GOING keep going}. In the first mode,
     * copy will stop at the first error encountered and this error will be
     * thrown. In the second mode, the copy will continue even if one or more
     * errors are encountered and this method will throw a {@link
     * RecursiveCopyException}. The list of exceptions which occured during the
     * copy operations are available via {@link Throwable#getSuppressed()}.</p>
     * <p/>
     * <p>The only supported {@link CopyOption copy option} is {@link
     * StandardCopyOption#REPLACE_EXISTING}.</p>
     *
     * @param source the source to copy (either a file or a directory)
     * @param destination the destination
     * @param mode the recursion mode (see description)
     * @param options the set of copy options
     * @throws NoSuchFileException source does not exist; or a parent of the
     * destination does not exist when attempting to create the destination
     * @throws UnsupportedOperationException unsupported copy option; or a
     * symbolic link was encountered during copy
     * @throws FileAlreadyExistsException {@link
     * StandardCopyOption#REPLACE_EXISTING} was not specified, and the
     * destination path already exists
     * @throws DirectoryNotEmptyException {@link
     * StandardCopyOption#REPLACE_EXISTING} was specified but the destination
     * path is a non empty directory
     * @throws RecursiveCopyException {@link RecursionMode#KEEP_GOING} was
     * specified, however one or more errors were encountered during the copy
     * operation
     * @throws IOException other I/O errors (access denied, etc)
     * @see MorePaths#resolve(Path, Path)
     * @see Files#walkFileTree(Path, FileVisitor)
     * @see Files#copy(Path, Path, CopyOption...)
     * @see FailFastCopyVisitor
     * @see KeepGoingCopyVisitor
     */
    public static void copyRecursive(final Path source, final Path destination,
        final RecursionMode mode, final CopyOption... options)
        throws IOException
    {
        Objects.requireNonNull(mode);

        boolean replace = false;

        for (final CopyOption option : options) {
            Objects.requireNonNull(option);
            if (option == StandardCopyOption.REPLACE_EXISTING) {
                replace = true;
            }
        }

        // We only support one option; array must be empty or have one element
        if (options.length > 1) {
            throw new UnsupportedOperationException();
        }

        // This will throw NoSuchFileException for us if source does not exist
        final Path src = Objects.requireNonNull(source).toRealPath();
        final Path dst = Objects.requireNonNull(destination).toAbsolutePath();

        if (Files.exists(dst, LinkOption.NOFOLLOW_LINKS) && !replace) {
            throw new FileAlreadyExistsException(destination.toString());
        }

        Files.deleteIfExists(dst);

        if (mode == RecursionMode.FAIL_FAST) {
            Files.walkFileTree(src, new FailFastCopyVisitor(src, dst));
            return;
        }

        // Cannot happen in theory, but...
        if (mode != RecursionMode.KEEP_GOING) {
            throw new IllegalStateException();
        }

        final RecursiveCopyException e = new RecursiveCopyException();
        final FileVisitor<Path> visitor = new KeepGoingCopyVisitor(src, dst, e);

        Files.walkFileTree(src, visitor);
        if (e.getSuppressed().length != 0) {
            throw e;
        }
    }


    /**
     * Delete a path recursively
     * <p/>
     * <p>Note that if a symbolic link is encountered, the symbolic link itself
     * will be deleted. The target (if valid) is left untouched.</p>
     * <p/>
     * <p>There are two modes of operation: {@link RecursionMode#FAIL_FAST fail
     * fast} and {@link RecursionMode#KEEP_GOING keep going}. In the first mode,
     * deletion will stop at the first error encountered and throw the
     * exception. In the second mode, deletion will continue, and this method
     * will throw a {@link RecursiveDeletionException}. The list of exceptions
     * encountered during the deletion operation is available using {@link
     * Throwable#getSuppressed()}.</p>
     *
     * @param victim the victim
     * @param mode the recursion mode (see description)
     * @throws NoSuchFileException victim does not exist (lucky you)
     * @throws RecursiveDeletionException {@link RecursionMode#KEEP_GOING} was
     * specified, and one or more errors were encountered during the deletion
     * operation (see description)
     * @throws IOException other I/O errors
     * @see FailFastDeletionVisitor
     * @see KeepGoingDeletionVisitor
     * @see Files#delete(Path)
     */
    public static void deleteRecursive(final Path victim,
        final RecursionMode mode)
        throws IOException
    {
        Objects.requireNonNull(victim);
        Objects.requireNonNull(mode);

        final FileVisitor<Path> visitor;

        switch (mode) {
            case KEEP_GOING:
                final RecursiveDeletionException exception
                    = new RecursiveDeletionException();
                visitor = new KeepGoingDeletionVisitor(victim, exception);
                Files.walkFileTree(victim, visitor);
                if (exception.getSuppressed().length != 0) {
                    throw exception;
                }
                break;
            case FAIL_FAST:
                visitor = new FailFastDeletionVisitor(victim);
                Files.walkFileTree(victim, visitor);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Change POSIX file permissions of a path
     * <p/>
     * <p>This method will take an integer as an argument, just like the Unix
     * {@code chmod} command, with one difference: you <em>must</em> prefix the
     * integer mode with {@code 0} so that Java read it as an octal number; that
     * is, use {@code setMode(myPath, 0644)} and <em>not</em> {@code
     * setMode(myPath, 644)}.</p>
     *
     * @param path the path to change
     * @param mode the permissions to set, as an integer
     * @return the path
     *
     * @throws InvalidIntModeException integer mode is not valid
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with this path does not support POSIX file
     * permissions
     * @throws IOException failed to set the modes
     * @see Files#setPosixFilePermissions(Path, Set)
     * @see PosixModes#intModeToPosix(int)
     */
    @Nonnull
    public static Path setMode(final Path path, final int mode)
        throws IOException
    {
        Objects.requireNonNull(path);
        final Set<PosixFilePermission> perms = PosixModes.intModeToPosix(mode);

        return Files.setPosixFilePermissions(path, perms);
    }

    /**
     * Change POSIX file permissions of a path
     * <p/>
     * <p>This command accepts a string representing the absolute POSIX
     * permissions to set; for instance, {@code setMode(thePath, "rw-r-----")}.
     * </p>
     * <p/>
     * <p>Only classical modes are supported; you cannot set the suid bit nor
     * sticky bit using this command. Attempting to do so will throw an {@link
     * IllegalArgumentException}.</p>
     *
     * @param path the path to alter
     * @param permissions the permissions to set, as a mode string
     * @return the path
     *
     * @throws IllegalArgumentException invalid permission string
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with this path does not support POSIX file
     * permissions
     * @throws IOException failed to set the modes
     * @see PosixFilePermissions#fromString(String)
     * @see Files#setPosixFilePermissions(Path, Set)
     */
    @Nonnull
    public static Path setMode(final Path path, final String permissions)
        throws IOException
    {
        Objects.requireNonNull(permissions);
        Objects.requireNonNull(path);

        final Set<PosixFilePermission> perms = PosixFilePermissions.fromString(
            permissions);

        return Files.setPosixFilePermissions(path, perms);
    }

    /**
     * Create a new file with a set of absolute POSIX permissions
     * <p/>
     * <p>{@link Files#createFile(Path, FileAttribute[])} can be used to set
     * initial POSIX file permissions; however, those permissions are altered
     * by the process' umask.</p>
     * <p/>
     * <p>This method is not "umask sensitive"; however, this means that unlike
     * the command from {@link Files}, it is not atomic either: the file is
     * first created, then the permissions are set.</p>
     *
     * @param path the file to create
     * @param permissions the permissions to create the file with, as a mode
     * string
     * @return the created path
     *
     * @throws IllegalArgumentException invalid mode string specified
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with this path does not support POSIX file
     * permissions
     * @throws FileAlreadyExistsException the path already exists
     * @throws IOException failed to create the file
     * @see PosixFilePermissions#fromString(String)
     * @see PosixFilePermissions#asFileAttribute(Set)
     */
    @Nonnull
    public static Path createFile(final Path path, final String permissions)
        throws IOException
    {
        Objects.requireNonNull(path);
        Objects.requireNonNull(permissions);

        final Set<PosixFilePermission> perms = PosixFilePermissions.fromString(
            permissions);

        return doCreateFile(path, perms);
    }

    /**
     * Create a new file with a set of absolute POSIX permissions
     * <p/>
     * <p>{@link Files#createFile(Path, FileAttribute[])} can be used to set
     * initial POSIX file permissions; however, those permissions are altered
     * by the process' umask.</p>
     * <p/>
     * <p>This method is not "umask sensitive"; however, this means that unlike
     * the command from {@link Files}, it is not atomic either: the file is
     * first created, then the permissions are set.</p>
     *
     * @param path the path to create
     * @param mode the permissions to create the file with, as an integer
     * @return the path
     *
     * @throws InvalidIntModeException invalid integer mode specified
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with this path does not support POSIX file
     * permissions
     * @throws FileAlreadyExistsException the path already exists
     * @throws IOException failed to create the file
     * @see PosixModes#intModeToPosix(int)
     * @see PosixFilePermissions#asFileAttribute(Set)
     */
    @Nonnull
    public static Path createFile(final Path path, final int mode)
        throws IOException
    {
        Objects.requireNonNull(path);

        final Set<PosixFilePermission> perms = PosixModes.intModeToPosix(mode);

        return doCreateFile(path, perms);
    }

    /**
     * Create a new directory with a set of absolute POSIX permissions
     * <p/>
     * <p>{@link Files#createDirectory(Path, FileAttribute[])}} can be used to
     * set initial POSIX file permissions; however, those permissions are
     * altered by the process' umask.</p>
     * <p/>
     * <p>This method is not "umask sensitive"; however, this means that unlike
     * the command from {@link Files}, it is not atomic either: the directory is
     * first created, then the permissions are set.</p>
     *
     * @param dir the directory to create
     * @param permissions the permissions to create the directory with, as a
     * mode string
     * @return the created path
     *
     * @throws IllegalArgumentException invalid mode string specified
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with this path does not support POSIX file
     * permissions
     * @throws FileAlreadyExistsException the path already exists
     * @throws IOException failed to create the directory
     * @see PosixFilePermissions#fromString(String)
     * @see PosixFilePermissions#asFileAttribute(Set)
     */
    @Nonnull
    public static Path createDirectory(final Path dir, final String permissions)
        throws IOException
    {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(permissions);

        final Set<PosixFilePermission> perms = PosixFilePermissions.fromString(
            permissions);

        return doCreateDirectory(dir, perms);
    }

    /**
     * Create a new directory with a set of absolute POSIX permissions
     * <p/>
     * <p>{@link Files#createDirectory(Path, FileAttribute[])} can be used to
     * set initial POSIX file permissions; however, those permissions are
     * altered by the process' umask.</p>
     * <p/>
     * <p>This method is not "umask sensitive"; however, this means that unlike
     * the command from {@link Files}, it is not atomic either: the directory is
     * first created, then the permissions are set.</p>
     *
     * @param dir the directory to create
     * @param mode the permissions to create the directory with, as an integer
     * @return the created path
     *
     * @throws InvalidIntModeException invalid integer mode specified
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with this path does not support POSIX file
     * permissions
     * @throws FileAlreadyExistsException the path already exists
     * @throws IOException failed to create the directory
     * @see PosixModes#intModeToPosix(int)
     * @see PosixFilePermissions#asFileAttribute(Set)
     */
    @Nonnull
    public static Path createDirectory(final Path dir, final int mode)
        throws IOException
    {
        Objects.requireNonNull(dir);

        final Set<PosixFilePermission> perms = PosixModes.intModeToPosix(mode);

        return doCreateDirectory(dir, perms);
    }

    /**
     * Create a new directory and all its missing parents with a set of absolute
     * POSIX permissions
     * <p/>
     * <p>{@link Files#createDirectories(Path, FileAttribute[])}} can be used to
     * set initial POSIX file permissions; however, those permissions are
     * altered by the process' umask.</p>
     * <p/>
     * <p>This method is not "umask sensitive"; however, this means that unlike
     * the command from {@link Files}, it is not atomic either: the
     * directories are first created, then the permissions are set.</p>
     * <p/>
     * <p>The permissions of already existing directories are
     * <strong>not</strong> altered.</p>
     *
     * @param dir the directory (and its missing parents) to create
     * @param permissions the permissions to create the missing directories4
     * with, as a mode string
     * @return the created path
     *
     * @throws IllegalArgumentException invalid mode string specified
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with this path does not support POSIX file
     * permissions
     * @throws IOException failed to create the directory
     * @see PosixFilePermissions#fromString(String)
     * @see PosixFilePermissions#asFileAttribute(Set)
     */
    @Nonnull
    public static Path createDirectories(final Path dir,
        final String permissions)
        throws IOException
    {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(permissions);

        final Path realDir = dir.toAbsolutePath();
        final Set<PosixFilePermission> perms = PosixFilePermissions.fromString(
            permissions);

        doCreateDirectories(realDir, perms);

        return dir;
    }

    /**
     * Create a new directory and all its missing parents with a set of absolute
     * POSIX permissions
     * <p/>
     * <p>{@link Files#createDirectories(Path, FileAttribute[])}} can be used to
     * set initial POSIX file permissions; however, those permissions are
     * altered by the process' umask.</p>
     * <p/>
     * <p>This method is not "umask sensitive"; however, this means that unlike
     * the command from {@link Files}, it is not atomic either: the
     * directories are first created, then the permissions are set.</p>
     * <p/>
     * <p>The permissions of already existing directories are
     * <strong>not</strong> altered.</p>
     *
     * @param dir the directory (and its missing parents) to create
     * @param mode the permissions to create the missing directories
     * with, as an integer
     * @return the created path
     *
     * @throws InvalidIntModeException invalid integer mode specified
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with this path does not support POSIX file
     * permissions
     * @throws IOException failed to create the directory
     * @see PosixModes#intModeToPosix(int)
     * @see PosixFilePermissions#asFileAttribute(Set)
     */
    @Nonnull
    public static Path createDirectories(final Path dir, final int mode)
        throws IOException
    {
        Objects.requireNonNull(dir);

        final Path realDir = dir.toAbsolutePath();
        final Set<PosixFilePermission> perms = PosixModes.intModeToPosix(mode);

        doCreateDirectories(realDir, perms);

        return dir;
    }

    /**
     * Update the last access and modification time of a file, or create a file
     * <p/>
     * <p>This command works similarly to the Unix {@code touch} command. If the
     * given path does not exist, it is created (as an empty {@link
     * Files#isRegularFile(Path, LinkOption...) regular file}); otherwise, its
     * last access and modification time are set.</p>
     *
     * @param path the path to alter/create
     * @return the altered/created path
     *
     * @throws UnsupportedOperationException the {@link Path#getFileSystem()
     * filesystem} associated with the path, or the associated filesystem object
     * associated with the path, does not support setting file times
     * @throws IOException cannot update the times and/or create the file; other
     * reasons
     * @see System#currentTimeMillis()
     * @see FileTime#fromMillis(long)
     * @see Files#createFile(Path, FileAttribute[])
     * @see BasicFileAttributeView#setTimes(FileTime, FileTime, FileTime)
     */
    @Nonnull
    public static Path touch(final Path path)
        throws IOException
    {
        if (!Files.exists(Objects.requireNonNull(path))) {
            return Files.createFile(path);
        }

        final FileTime time = FileTime.fromMillis(System.currentTimeMillis());
        return setTimes(path, time);
    }

    /**
     * Change the posix permissions of a file/directory using a chmod-like
     * modification string
     * <p/>
     * <p>The modification string is the same as {@code chmod}. For instance:
     * </p>
     * <p/>
     * <pre>
     *     MoreFiles.changeMode(path, "o-rwx,g+r,g-w");
     * </pre>
     * <p/>
     * <p>See {@link ModeParser#buildPermissionsSet(String)} for the list of
     * supported constructs.</p>
     *
     * @param target the target to alter
     * @param instructions the modification instructions
     * @return the target path
     *
     * @throws UnsupportedOperationException the target's filesystem does not
     * support getting/setting posix permissions
     * @throws IOException failed to change the permissions
     */
    @Nonnull
    public static Path changeMode(final Path target, final String instructions)
        throws IOException
    {
        final PermissionsSet set = ModeParser.buildPermissionsSet(instructions);

        final Set<PosixFilePermission> before = Files.getPosixFilePermissions(
            target);

        final Set<PosixFilePermission> after = set.modify(before);

        return Files.setPosixFilePermissions(target, after);
    }

    // Visible for testing
    @Nonnull
    static Path setTimes(final Path path, final FileTime fileTime)
        throws IOException
    {
        final BasicFileAttributeView view = Files.getFileAttributeView(path,
            BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        view.setTimes(fileTime, fileTime, null);
        return path;
    }

    private static Path doCreateFile(final Path path,
        final Set<PosixFilePermission> perms)
        throws IOException
    {
        Files.createFile(path);
        return Files.setPosixFilePermissions(path, perms);
    }

    private static Path doCreateDirectory(final Path dir,
        final Set<PosixFilePermission> perms)
        throws IOException
    {
        Files.createDirectory(dir);
        return Files.setPosixFilePermissions(dir, perms);
    }

    private static void doCreateDirectories(final Path realDir,
        final Set<PosixFilePermission> perms)
        throws IOException
    {
        final List<Path> created = new ArrayList<>();

        Path parent = realDir;

        while (parent != null && !Files.exists(parent)) {
            created.add(parent);
            parent = parent.getParent();
        }

        Files.createDirectories(realDir);

        for (final Path path : created) {
            Files.setPosixFilePermissions(path, perms);
        }
    }

    /**
     * Opens a zip file on the FileSystem
     * <p/>
     * <p>StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW,
     * LinkOption.NOFOLLOW_LINKS can be injected right now.
     * </p>
     *
     * @param path the path to open zip from
     * @param options StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW,
     * LinkOption.NOFOLLOW_LINKS
     * @return returns the FileSystem of zip
     *
     * @throws UnsupportedOperationException any option other than above
     * * mentioned
     * is provided
     * @throws FileAlreadyExistsException CREATE_NEW option is provided and
     * @throws IOException otherwise TODO: Just have to know why it's so??
     */

    @Nonnull
    public static FileSystem openZip(final Path path,
        final OpenOption... options)
        throws IOException
    {

        boolean isCreateNew = false;
        boolean anyCreateOption = false;

        Objects.requireNonNull(path);

        for (OpenOption option : options) {
            Objects.requireNonNull(option);
            if (!VALID_OPTIONS.contains(option)) {
                throw new UnsupportedOperationException(
                    "option is not " + "supported");
            }

            if (option.equals(StandardOpenOption.CREATE_NEW)) {
                isCreateNew = true;
                anyCreateOption = true;
            }
            if (option.equals(StandardOpenOption.CREATE)) {
                anyCreateOption = true;
            }

            /**
             * Provided option is LinkOption.NOFOLLOW_LINKS and path is a 
             * * symbolic link
             * throws IOException
             */

            if (option.equals(LinkOption.NOFOLLOW_LINKS)) {
                if (Files.isSymbolicLink(path)) {
                    throw new IOException(
                        "refusing to open a symbolic link as a zip file");
                }
            }

            // /If path is a directory, throw IsDirectoryException
            if (Files.isDirectory(path)) {
                throw new IsDirectoryException(
                    "refusing to open a directory as a zip file");
            }
        }

        final boolean fileExists = Files.exists(path);

        if (isCreateNew && fileExists) {
            throw new FileAlreadyExistsException(path.toString());
        }

        /**
         * Path points to a zip file which doesn't exist but no create option
         * * is specified
         * throws NoSuchFileException
         */

        if (!anyCreateOption && !fileExists)
            throw new NoSuchFileException("no such zip file");


        /**
         * Create option is specified and if path exists, 
         * * return already existed zip filesystem, otherwise create new 
         * * zip filesystem for the path and return
         */
        final URI zipURI = URI.create("jar:" + path.toUri().toString());
        final Map<String, String> env = Collections.singletonMap("create",
            String.valueOf(!fileExists));

        return FileSystems.newFileSystem(zipURI, env);
    }

}