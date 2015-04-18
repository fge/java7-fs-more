package com.github.fge.filesystem.content;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class BinaryContentModifier
    extends ContentModifier
{
    private final InputStream in;
    private final OutputStream out;

    private volatile ContentInputStream wrappedIn = null;
    private volatile ContentOutputStream wrappedOut = null;

    public BinaryContentModifier(final Path toModify, final Path tempfile)
        throws IOException
    {
        super(toModify, tempfile);
        in = Files.newInputStream(toModify, StandardOpenOption.READ);
        out = Files.newOutputStream(tempfile, StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public InputStream getInputStream()
    {
        synchronized (in) {
            if (wrappedIn == null)
                wrappedIn = new ContentInputStream(this, in);
        }

        return wrappedIn;
    }

    @Override
    public OutputStream getOutputStream()
    {
        synchronized (out) {
            if (wrappedOut == null)
                wrappedOut = new ContentOutputStream(this, out);
        }

        return wrappedOut;
    }

    @Override
    protected void closeInput()
        throws IOException
    {
        wrappedIn.close();
    }

    @Override
    protected void closeOutput()
        throws IOException
    {
        wrappedOut.close();
    }

    @Override
    public void flush()
        throws IOException
    {
        wrappedOut.flush();
    }
}
