/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.yagi.framework.nodes.snakeyaml;

import com.google.common.collect.EvictingQueue;
import org.yaml.snakeyaml.reader.StreamReader;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Created. There, you have it.
 */
class SmartReader extends Reader
{

    private final Reader delegate;
    private char[] lastBufferRead;
    private int lastBufferSize;
    private int lastOffset;

    public SmartReader(Reader delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public int read(CharBuffer target) throws IOException
    {
        return delegate.read(target);
    }

    @Override
    public int read() throws IOException
    {
        return delegate.read();
    }

    @Override
    public int read(char[] cbuf) throws IOException
    {
        return read(cbuf, 0, cbuf.length);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        try
        {
            int numberOfRead = delegate.read(cbuf, off, len);
            lastBufferRead = cbuf;
            lastOffset = off;
            lastBufferSize = numberOfRead;
            return numberOfRead;
        }
        catch (IOException e)
        {

            lastBufferSize = 0;
            throw e;
        }
    }

    @Override
    public long skip(long n) throws IOException
    {
        return delegate.skip(n);
    }

    @Override
    public boolean ready() throws IOException
    {
        return delegate.ready();
    }

    @Override
    public boolean markSupported()
    {
        return delegate.markSupported();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException
    {
        delegate.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException
    {
        delegate.reset();
    }

    @Override
    public void close() throws IOException
    {
        delegate.close();
    }

    public String getLastValidString()
    {

        EvictingQueue<Character> seen = EvictingQueue.create(64);

        for (int i = lastOffset; i < this.lastBufferSize; i++)
        {
            if (!StreamReader.isPrintable(this.lastBufferRead[i]))
            {


                for (char c : ("[BAD: " + this.lastBufferRead[i] + "]").toCharArray())
                {

                    seen.add(c);
                }

                break;
            }

            seen.add(this.lastBufferRead[i]);
        }

        Character[] foo = seen.toArray(new Character[0]);
        StringBuilder sb = new StringBuilder();
        for (Character character : foo)
        {

            sb.append(character);
        }

        return sb.toString();
    }
}
