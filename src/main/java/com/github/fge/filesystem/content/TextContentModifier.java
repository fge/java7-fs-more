package com.github.fge.filesystem.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TextContentModifier
    extends ContentModifier
{
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public TextContentModifier(final Path toModify, final Charset charset)
        throws IOException
    {
        super(toModify);
        reader = Files.newBufferedReader(toModify, charset);
        writer = Files.newBufferedWriter(tempfile, charset);
    }

    @Override
    public BufferedReader getBufferedReader()
    {
        return reader;
    }

    @Override
    public BufferedWriter getBufferedWriter()
    {
        return writer;
    }

    @Override
    protected void closeInput()
        throws IOException
    {
        reader.close();
    }

    @Override
    protected void closeOutput()
        throws IOException
    {
        writer.close();
    }

    @Override
    public void flush()
        throws IOException
    {
        writer.flush();
    }
}
