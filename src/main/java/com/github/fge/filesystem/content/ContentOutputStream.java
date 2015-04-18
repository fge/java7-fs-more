package com.github.fge.filesystem.content;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.OutputStream;

@ParametersAreNonnullByDefault
final class ContentOutputStream
    extends OutputStream
{
    private final ContentModifier modifier;
    private final OutputStream out;

    ContentOutputStream(final ContentModifier modifier,
        final OutputStream out)
    {
        this.modifier = modifier;
        this.out = out;
    }

    @Override
    public void write(final int b)
        throws IOException
    {
        try {
            out.write(b);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void write(final byte[] b)
        throws IOException
    {
        try {
            out.write(b);
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void write(final byte[] b, final int off, final int len)
        throws IOException
    {
        try {
            out.write(b, off, len);
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
            out.flush();
        } catch (IOException e) {
            modifier.ioFailed();
            throw e;
        }
    }

    @Override
    public void close()
        throws IOException
    {
        out.close();
    }
}
