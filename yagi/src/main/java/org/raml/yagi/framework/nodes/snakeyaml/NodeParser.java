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
import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.DefaultPosition;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.reader.StreamReader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;

import static org.apache.commons.io.IOUtils.LINE_SEPARATOR_UNIX;
import static org.apache.commons.io.IOUtils.LINE_SEPARATOR_WINDOWS;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

public class NodeParser
{

    static class SmartReader extends Reader
    {

        private final Reader delegate;
        private char[] lastBufferRead;
        private int lastBufferSize;

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

            try
            {
                int numberOfRead = delegate.read(cbuf);
                lastBufferRead = cbuf;
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
        public int read(char[] cbuf, int off, int len) throws IOException
        {
            return delegate.read(cbuf, off, len);
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

            for (int i = 0; i < this.lastBufferSize; i++)
            {


                /*
                 * if ( Character.isISOControl(this.lastBufferRead[i])) {
                 * 
                 * seen.add('['); String s = Character.getName(this.lastBufferRead[i]); for (char c : s.toCharArray()) {
                 * 
                 * seen.add(c); }
                 * 
                 * continue; }
                 */

                if (!StreamReader.isPrintable(this.lastBufferRead[i]))
                {


                    for (char c : ("[BAD: " + Character.getName(this.lastBufferRead[i]) + "]").toCharArray())
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

    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, Reader reader)
    {

        SmartReader smartReader = new SmartReader(reader);

        try
        {
            Yaml yamlParser = new Yaml();
            org.yaml.snakeyaml.nodes.Node composedNode = yamlParser.compose(smartReader);
            if (composedNode == null)
            {
                return null;
            }
            else
            {
                return new SYModelWrapper(resourceLoader, resourcePath).wrap(composedNode);
            }
        }
        catch (final MarkedYAMLException e)
        {
            return buildYamlErrorNode(e, resourcePath);
        }
        catch (ReaderException e)
        {

            return buildYamlErrorNode(e, smartReader);
        }
    }

    private static Node buildYamlErrorNode(MarkedYAMLException e, String resourcePath)
    {
        final ErrorNode errorNode = new ErrorNode("Underlying error while parsing YAML syntax: '" + e.getMessage() + "'");
        final Mark problemMark = e.getProblemMark();
        errorNode.setStartPosition(new DefaultPosition(problemMark.getIndex(), problemMark.getLine(), 0, resourcePath, new DefaultResourceLoader()));
        errorNode.setEndPosition(new DefaultPosition(problemMark.getIndex() + 1, problemMark.getLine(), problemMark.getColumn(), resourcePath, new DefaultResourceLoader()));
        return errorNode;
    }

    private static Node buildYamlErrorNode(ReaderException e, SmartReader reader)
    {

        final ErrorNode errorNode = new ErrorNode("Underlying error while parsing YAML syntax: '" + e.getMessage() + " around '" + reader.getLastValidString() + "'");
        // errorNode.setStartPosition(new DefaultPosition(e.getPosition(), 0, 0, resourcePath, new DefaultResourceLoader()));
        // errorNode.setEndPosition(new DefaultPosition(e.getPosition() + 1, 0,0, resourcePath, new DefaultResourceLoader()));
        return errorNode;
    }

    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, String content)
    {
        if (IS_OS_WINDOWS)
        {
            content = content.replace(LINE_SEPARATOR_WINDOWS, LINE_SEPARATOR_UNIX);
        }

        return parse(resourceLoader, resourcePath, new StringReader(content));
    }


}
