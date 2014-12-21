package com.github.fge.filesystem.content;

import com.github.fge.filesystem.exceptions.RenameFailureException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Write/rename implementation
 *
 * <p>This class takes a file as an argument which you wish to modify the
 * contents of, and ensures a safe write/rename idiom.</p>
 *
 * <p>That is, if writing the new content fails, your original file will
 * <strong>not</strong> be altered in any way. There are two implementations,
 * one for reading/writing binary content and the other for reading/writing
 * text content. For the latter, specifying the {@link Charset} to use is
 * <strong>required</strong>.</p>
 *
 * <p>The methods to read content from the file (either {@link
 * #getInputStream() binary} or {@link #getBufferedReader() text} will read from
 * the original file, which is opened read only; the methods to write contents
 * (either {@link #getOutputStream() binary} or {@link #getBufferedWriter()
 * text}) will write to a temporary file, created in the same directory as the
 * original file.</p>
 *
 */
@SuppressWarnings({ "DesignForExtension", "MethodMayBeStatic" })
public abstract class ContentModifier
    implements Closeable, Flushable
{
    protected final Path toModify;
    protected final Path tempfile;

    private volatile boolean ioFailed = false;

    protected ContentModifier(final Path toModify)
        throws IOException
    {
        this.toModify = toModify;
        // Guaranteed to exist; we know that toModify is a regular file here
        tempfile = Files.createTempFile(toModify.getParent(), "replace", "tmp");
    }

    public InputStream getInputStream()
    {
        throw new IllegalArgumentException("file opened as text");
    }

    public OutputStream getOutputStream()
    {
        throw new IllegalArgumentException("file opened as text");
    }

    public BufferedReader getBufferedReader()
    {
        throw new IllegalArgumentException("file opened as binary");
    }

    public BufferedWriter getBufferedWriter()
    {
        throw new IllegalArgumentException("file opened as binary");
    }

    final void ioFailed()
    {
        ioFailed = true;
    }

    protected abstract void closeInput()
        throws IOException;

    protected abstract void closeOutput()
        throws IOException;

    @Override
    public final void close()
        throws IOException
    {
        IOException exception = ioFailed
            ? new IOException("one or more I/O operation(s) failed, will not "
                + "rename")
            : null;

        try {
            closeInput();
        } catch (IOException e) {
            if (exception == null)
                exception = e;
            else
                exception.addSuppressed(e);
        }

        try {
            closeOutput();
        } catch (IOException e) {
            if (exception == null)
                exception = e;
            else
                exception.addSuppressed(e);
        }

        if (exception != null)
            throw exception;

        try {
            //noinspection NestedTryStatement
            try {
                Files.move(tempfile, toModify,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(tempfile, toModify, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RenameFailureException(e);
        }
    }
}
