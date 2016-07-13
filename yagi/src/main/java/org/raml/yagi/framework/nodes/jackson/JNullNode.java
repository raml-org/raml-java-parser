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
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.NullNode;

import javax.annotation.Nonnull;

public class JNullNode extends JBaseRamlNode implements NullNode
{

    public JNullNode(com.fasterxml.jackson.databind.node.NullNode jsonNode, String resourcePath, ResourceLoader resourceLoader)
    {
        super(jsonNode, resourcePath, resourceLoader);
    }

    protected JNullNode(JNullNode node)
    {
        super(node);
    }

    @Nonnull
    @Override
    public org.raml.yagi.framework.nodes.Node copy()
    {
        return new JNullNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Null;
    }

    @Override
    public String toString()
    {
        return "JNullNode";
    }
}
