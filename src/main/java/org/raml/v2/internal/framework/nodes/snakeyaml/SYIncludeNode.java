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

import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.framework.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYIncludeNode extends SYStringNode
{

    private SYIncludeNode(SYIncludeNode node, String resourcePath)
    {
        super(node, resourcePath);
    }

    public SYIncludeNode(ScalarNode scalarNode, String resourcePath)
    {
        super(scalarNode, resourcePath);
    }

    public String getIncludePath()
    {
        Node current = this;
        while (current != null)
        {
            Node possibleSource = current.getSource();
            if (possibleSource instanceof SYIncludeNode)
            {
                String basePath = ((SYIncludeNode) possibleSource).getIncludePath();
                List<String> segments = Lists.newArrayList(basePath.split("/"));
                if (segments.size() > 1)
                {
                    segments.remove(segments.size() - 1);
                    return StringUtils.join(segments, "/") + "/" + getValue();
                }
                else
                {
                    return getValue();
                }
            }
            current = current.getParent();
        }

        return getValue();
    }

    public String getIncludedType()
    {
        String parts[];
        parts = this.getValue().split("#");
        if (parts.length == 2)
        {
            return parts[1];
        }
        else
            return null;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new SYIncludeNode(this, getResourcePath());
    }
}
