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

import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.DefaultPosition;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import javax.annotation.Nullable;
import java.io.Reader;
import java.io.StringReader;

import static org.apache.commons.io.IOUtils.LINE_SEPARATOR_UNIX;
import static org.apache.commons.io.IOUtils.LINE_SEPARATOR_WINDOWS;
import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

public class NodeParser
{


    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, Reader reader)
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
                return new SYModelWrapper(resourceLoader, resourcePath).wrap(composedNode);
            }
        }
        catch (final MarkedYAMLException e)
        {
            return buildYamlErrorNode(e, resourcePath);
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
