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

import javax.annotation.Nonnull;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.StringNode;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SYStringNode extends SYBaseRamlNode implements StringNode
{

    // For copy
    protected SYStringNode(SYStringNode node)
    {
        super(node);
    }


    public SYStringNode(ScalarNode scalarNode, String resourcePath, ResourceLoader resourceLoader)
    {
        super(scalarNode, resourcePath, resourceLoader);
    }

    public String getValue()
    {
        return getLiteralValue();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new SYStringNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.String;
    }
}
