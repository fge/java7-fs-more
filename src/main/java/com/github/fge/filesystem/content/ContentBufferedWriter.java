package com.github.fge.filesystem.content;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

@ParametersAreNonnullByDefault
final class ContentBufferedWriter
    extends BufferedWriter
{
    private final ContentModifier modifier;

    ContentBufferedWriter(final Writer out,
        final ContentModifier modifier)
    {
        super(out);
        this.modifier = modifier;
    }

    ContentBufferedWriter(final Writer out, final int sz,
        final ContentModifier modifier)
    {
        super(out, sz);
        this.modifier = modifier;
    }

    @Override
    public void write(final int c)
        throws IOException
    {
        try {
            super.write(c);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len)
        throws IOException
    {
        try {
            super.write(cbuf, off, len);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void write(final String s, final int off, final int len)
        throws IOException
    {
        try {
            super.write(s, off, len);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void newLine()
        throws IOException
    {
        try {
            super.newLine();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void flush()
        throws IOException
    {
        try {
            super.flush();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void write(final char[] cbuf)
        throws IOException
    {
        try {
            super.write(cbuf);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void write(final String str)
        throws IOException
    {
        try {
            super.write(str);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public Writer append(final CharSequence csq)
        throws IOException
    {
        try {
            return super.append(csq);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public Writer append(final CharSequence csq, final int start, final int end)
        throws IOException
    {
        try {
            return super.append(csq, start, end);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public Writer append(final char c)
        throws IOException
    {
        try {
            return super.append(c);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }
}
