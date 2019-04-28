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
package org.raml.v2.internal.utils;

import org.raml.v2.internal.utils.RamlTreeNodeDumper;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.util.NodeAppender;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

/**
 * Created. There, you have it.
 */
public class Dumper
{
    public static String inMemoryDumper(Node raml)
    {
        StringWriter sw = new StringWriter();
        RamlTreeNodeDumper dumper = new RamlTreeNodeDumper(sw);
        dumper.dump(raml);
        return sw.toString().trim();
    }

    public static void straightOutput(Node raml)
    {
        final OutputStreamWriter writer = new OutputStreamWriter(System.out);

        RamlTreeNodeDumper dumper = new RamlTreeNodeDumper(new NodeAppender()
        {
            @Override
            public NodeAppender append(Object o)
            {

                try
                {
                    writer.append(o.toString());
                    writer.flush();
                    return this;
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public NodeAppender append(String s)
            {
                try
                {
                    writer.append(s);
                    writer.flush();
                    return this;
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public NodeAppender append(int s)
            {

                try
                {
                    writer.append(Integer.toString(s));
                    writer.flush();
                    return this;
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public NodeAppender dump()
            {
                return this;
            }
        });
        dumper.dump(raml);
    }

}
