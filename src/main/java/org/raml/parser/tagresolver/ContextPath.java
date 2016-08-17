/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.tagresolver;

import java.util.ArrayDeque;
import java.util.Deque;

import org.raml.parser.visitor.IncludeInfo;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class ContextPath
{

    private Deque<IncludeInfo> includeStack = new ArrayDeque<IncludeInfo>();

    public ContextPath()
    {
    }

    public ContextPath(ContextPath contextPath)
    {
        this.includeStack = new ArrayDeque<IncludeInfo>(contextPath.includeStack);
    }

    public ContextPath(IncludeInfo includeInfo)
    {
        includeStack.add(includeInfo);
    }

    public void pushRoot(String absoluteFile)
    {
        if (includeStack.size() > 0)
        {
            throw new IllegalStateException("Non empty stack");
        }
        includeStack.add(new IncludeInfo(absoluteFile));
    }

    public static String resolveAbsolutePath(String relativeFile, String parentPath)
    {
        //check if it is absolute
        if (relativeFile.startsWith("classpath:"))
        {
            return relativeFile.substring(relativeFile.indexOf(":") + 1);
        }
        if (relativeFile.startsWith("http:") ||
            relativeFile.startsWith("https:") ||
            relativeFile.startsWith("file:"))
        {
            return relativeFile;
        }
        return parentPath + relativeFile;
    }

    public String resolveAbsolutePath(String relativeFile)
    {
        return resolveAbsolutePath(relativeFile, getPartentPath());
    }

    private String getPartentPath()
    {
        int idx = includeStack.peek().getIncludeName().lastIndexOf("/") + 1;
        return includeStack.peek().getIncludeName().substring(0, idx);
    }

    public IncludeInfo peek()
    {
        return includeStack.peek();
    }

    public IncludeInfo pop()
    {
        return includeStack.pop();
    }

    public void push(IncludeInfo includeInfo)
    {
        includeStack.push(includeInfo);
    }

    public void push(ScalarNode node)
    {
        push(new IncludeInfo(node, getPartentPath()));
    }

    public void push(Tag tag)
    {
        push(new IncludeInfo(tag));
    }

    public int size()
    {
        return includeStack.size();
    }
}
