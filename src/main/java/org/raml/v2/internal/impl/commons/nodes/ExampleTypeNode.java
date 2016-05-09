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
package org.raml.v2.internal.impl.commons.nodes;

import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.grammar.rule.AnyValueRule;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.BooleanTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.DateTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.FloatTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.IntegerTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.StringTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.TypeNodeVisitor;
import org.raml.v2.internal.framework.nodes.AbstractRamlNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.utils.JSonDumper;
import org.raml.v2.internal.utils.NodeUtils;

public class ExampleTypeNode extends AbstractRamlNode implements ObjectNode, TypeNode
{

    public ExampleTypeNode()
    {
    }

    protected ExampleTypeNode(AbstractRamlNode node)
    {
        super(node);
    }

    @Override
    public <T> T visit(TypeNodeVisitor<T> visitor)
    {
        return ((TypeNode) this.getTypeNode()).visit(visitor);
    }

    public <T> T visitProperties(TypeNodeVisitor<T> visitor, List<PropertyNode> properties, boolean allowsAdditionalProperties, boolean strict)
    {
        return visitor.visitExample(properties, allowsAdditionalProperties, strict);
    }

    @Nonnull
    @Override
    public Node copy()
    {
        ExampleTypeNode exampleTypeNode = new ExampleTypeNode();
        exampleTypeNode.setSource(this.getSource().copy());
        return exampleTypeNode;
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    @Override
    public String toString()
    {
        return getSource().toString();
    }

    public String toJsonString()
    {
        return JSonDumper.dump(getSource());
    }

    public boolean isArrayExample()
    {
        Node type = NodeUtils.getType(this.getParent().getParent());
        return type instanceof StringNode && "array".equals(((StringNode) type).getValue());
    }

    public Node getTypeNode()
    {
        return this.getParent().getParent();
    }
}
