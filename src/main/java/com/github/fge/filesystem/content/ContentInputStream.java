package com.github.fge.filesystem.content;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;

@ParametersAreNonnullByDefault
final class ContentInputStream
    extends InputStream
{
    private final ContentModifier modifier;
    private final InputStream in;

    ContentInputStream(final ContentModifier modifier,
        final InputStream in)
    {
        this.modifier = modifier;
        this.in = in;
    }

    @Override
    public int read()
        throws IOException
    {
        try {
            return in.read();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public int read(final byte[] b)
        throws IOException
    {
        try {
            return in.read(b);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public int read(final byte[] b, final int off, final int len)
        throws IOException
    {
        try {
            return in.read(b, off, len);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public long skip(final long n)
        throws IOException
    {
        try {
            return in.skip(n);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public int available()
        throws IOException
    {
        try {
            return in.available();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void close()
        throws IOException
    {
        in.close();
    }

    @Override
    public synchronized void mark(final int readlimit)
    {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset()
        throws IOException
    {
        try {
            in.reset();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public boolean markSupported()
    {
        return in.markSupported();
    }
}
