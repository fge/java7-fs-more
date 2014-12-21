package com.github.fge.filesystem.content;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

@ParametersAreNonnullByDefault
final class ContentBufferedReader
    extends BufferedReader
{
    private final ContentModifier modifier;

    ContentBufferedReader(final Reader in, final int sz,
        final ContentModifier modifier)
    {
        super(in, sz);
        this.modifier = modifier;
    }

    ContentBufferedReader(final Reader in, final ContentModifier modifier)
    {
        super(in);
        this.modifier = modifier;
    }

    @Override
    public int read()
        throws IOException
    {
        try {
            return super.read();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public int read(final char[] cbuf, final int off, final int len)
        throws IOException
    {
        try {
            return super.read(cbuf, off, len);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public String readLine()
        throws IOException
    {
        try {
            return super.readLine();
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
            return super.skip(n);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public boolean ready()
        throws IOException
    {
        try {
            return super.ready();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void mark(final int readAheadLimit)
        throws IOException
    {
        try {
            super.mark(readAheadLimit);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void reset()
        throws IOException
    {
        try {
            super.reset();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public int read(final CharBuffer target)
        throws IOException
    {
        try {
            return super.read(target);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public int read(final char[] cbuf)
        throws IOException
    {
        try {
            return super.read(cbuf);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }
}
