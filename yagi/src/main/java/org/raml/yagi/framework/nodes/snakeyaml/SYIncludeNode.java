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

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.IncludeNode;
import org.raml.yagi.framework.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

import javax.annotation.Nonnull;

public class SYIncludeNode extends SYStringNode implements IncludeNode
{

    private SYIncludeNode(SYIncludeNode node)
    {
        super(node);
    }

    public SYIncludeNode(ScalarNode scalarNode, String resourcePath, ResourceLoader resourceLoader)
    {
        super(scalarNode, resourcePath, resourceLoader);
    }

    @Override
    public String getIncludePath()
    {
        return getValue();
    }

    @Override
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
        return new SYIncludeNode(this);
    }
}
