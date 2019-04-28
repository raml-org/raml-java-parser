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
package org.raml.yagi.framework.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Created. There, you have it.
 */
public class NodeAppenderFactory
{

    public static NodeAppender stringBuilder(final Writer output)
    {

        final StringBuilder sb = new StringBuilder();

        return new NodeAppender()
        {

            @Override
            public NodeAppender append(Object o)
            {
                sb.append(o);
                return this;
            }

            @Override
            public NodeAppender append(String s)
            {
                sb.append(s);
                return this;
            }

            @Override
            public NodeAppender append(int s)
            {
                sb.append(s);
                return this;
            }

            public NodeAppender dump()
            {

                try
                {
                    output.append(sb.toString());
                    sb.delete(0, sb.length());
                    return this;
                }
                catch (IOException e)
                {

                    throw new RuntimeException(e);
                }
            }
        };
    }
}
