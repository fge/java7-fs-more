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

    public BinaryContentModifier(final Path toModify)
        throws IOException
    {
        super(toModify);
        in = Files.newInputStream(toModify, StandardOpenOption.READ);
        out = Files.newOutputStream(tempfile, StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public InputStream getInputStream()
    {
        return in;
    }

    @Override
    public OutputStream getOutputStream()
    {
        return out;
    }

    @Override
    protected void closeInput()
        throws IOException
    {
        in.close();
    }

    @Override
    protected void closeOutput()
        throws IOException
    {
        out.close();
    }

    @Override
    public void flush()
        throws IOException
    {
        out.flush();
    }
}
