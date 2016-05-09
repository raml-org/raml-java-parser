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
package org.raml.v2.internal.impl.v10.nodes.types.builtin;

import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.framework.nodes.AbstractRamlNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYArrayNode;
import org.raml.v2.internal.utils.NodeSelector;

public class StringTypeNode extends AbstractRamlNode implements ObjectNode, TypeNode
{

    public StringTypeNode()
    {
    }

    private StringTypeNode(StringTypeNode node)
    {
        super(node);
    }

    @Nullable
    public Integer getMinLength()
    {
        return NodeSelector.selectIntValue("minLength", getSource());
    }

    @Nullable
    public Integer getMaxLength()
    {
        return NodeSelector.selectIntValue("maxLength", getSource());
    }

    @Nullable
    public String getPattern()
    {
        return NodeSelector.selectStringValue("pattern", getSource());
    }

    @Nonnull
    public List<String> getEnumValues()
    {
        Node values = this.get("enum");
        List<String> enumValues = Lists.newArrayList();
        if (values != null && values instanceof SYArrayNode)
        {
            for (Node node : values.getChildren())
            {
                enumValues.add(((StringNode) node).getValue());
            }
        }
        return enumValues;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new StringTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    @Override
    public <T> T visit(TypeNodeVisitor<T> visitor)
    {
        return visitor.visitString(this);
    }
}
