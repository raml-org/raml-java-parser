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
package org.raml.yagi.framework.nodes.jackson;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.BooleanNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;

import javax.annotation.Nonnull;

public class JBooleanNode extends JBaseRamlNode implements BooleanNode
{

    protected JBooleanNode(JBooleanNode node)
    {
        super(node);
    }

    public JBooleanNode(com.fasterxml.jackson.databind.node.BooleanNode booleanNode, String resourcePath, ResourceLoader resourceLoader)
    {
        super(booleanNode, resourcePath, resourceLoader);
    }

    @Override
    public Boolean getValue()
    {
        return getJsonNode().booleanValue();
    }

    @Override
    public String toString()
    {
        return String.valueOf(getValue());
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new JBooleanNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Boolean;
    }
}
