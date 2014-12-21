package com.github.fge.filesystem.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@SuppressWarnings({ "DesignForExtension", "MethodMayBeStatic" })
public abstract class ContentModifier
    implements Closeable, Flushable
{
    protected final Path toModify;
    protected final Path tempfile;

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

    protected abstract void closeInput()
        throws IOException;

    protected abstract void closeOutput()
        throws IOException;

    @Override
    public final void close()
        throws IOException
    {
        IOException exception = null;

        try {
            closeInput();
        } catch (IOException e) {
            exception = e;
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
            Files.move(tempfile, toModify, StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(tempfile, toModify, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
