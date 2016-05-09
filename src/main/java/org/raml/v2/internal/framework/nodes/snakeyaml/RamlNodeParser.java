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
package org.raml.v2.internal.framework.nodes.snakeyaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.annotation.Nullable;

import org.raml.v2.internal.framework.nodes.DefaultPosition;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.scanner.ScannerException;

public class RamlNodeParser
{

    @Nullable
    public static Node parse(InputStream inputStream)
    {
        return parse(inputStream, false);
    }

    @Nullable
    public static Node parse(InputStream inputStream, boolean supportLibraries)
    {
        try
        {
            return parse(new InputStreamReader(inputStream, "UTF-8"), supportLibraries);
        }
        catch (UnsupportedEncodingException e)
        {
            return parse(new InputStreamReader(inputStream), supportLibraries);
        }
    }


    @Nullable
    public static Node parse(Reader reader)
    {
        return parse(reader, false);
    }

    @Nullable
    public static Node parse(Reader reader, boolean supportLibraries)
    {
        try
        {
            Yaml yamlParser = new Yaml();
            org.yaml.snakeyaml.nodes.Node composedNode = yamlParser.compose(reader);
            if (composedNode == null)
            {
                return null;
            }
            else
            {
                return new SYModelWrapper(supportLibraries).wrap(composedNode);
            }
        }
        catch (final MarkedYAMLException e)
        {
            return buildYamlErrorNode(e);
        }
    }

    private static Node buildYamlErrorNode(MarkedYAMLException e)
    {
        final ErrorNode errorNode = new ErrorNode("Underlying error while parsing YAML syntax: '" + e.getMessage() + "'");
        final Mark problemMark = e.getProblemMark();
        errorNode.setStartPosition(new DefaultPosition(problemMark.getIndex(), problemMark.getLine(), 0, ""));
        errorNode.setEndPosition(new DefaultPosition(problemMark.getIndex() + 1, problemMark.getLine(), problemMark.getColumn(), ""));
        return errorNode;
    }

    @Nullable
    public static Node parse(String content)
    {
        return parse(new StringReader(content));
    }

    @Nullable
    public static Node parse(String content, boolean supportLibraries)
    {
        return parse(new StringReader(content), supportLibraries);
    }
}
