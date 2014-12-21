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

    private volatile ContentBufferedReader wrappedReader;
    private volatile ContentBufferedWriter wrappedWriter;

    public TextContentModifier(final Path toModify, final Path tempfile,
        final Charset charset)
        throws IOException
    {
        super(toModify, tempfile);
        reader = Files.newBufferedReader(toModify, charset);
        writer = Files.newBufferedWriter(tempfile, charset);
    }

    @Override
    public BufferedReader getBufferedReader()
    {
        synchronized (reader) {
            if (wrappedReader == null)
                wrappedReader = new ContentBufferedReader(reader, this);
        }

        return wrappedReader;
    }

    @Override
    public BufferedWriter getBufferedWriter()
    {
        synchronized (writer) {
            if (wrappedWriter == null)
                wrappedWriter = new ContentBufferedWriter(writer, this);
        }

        return wrappedWriter;
    }

    @Override
    protected void closeInput()
        throws IOException
    {
        wrappedReader.close();
    }

    @Override
    protected void closeOutput()
        throws IOException
    {
        wrappedWriter.close();
    }

    @Override
    public void flush()
        throws IOException
    {
        wrappedWriter.flush();
    }
}
