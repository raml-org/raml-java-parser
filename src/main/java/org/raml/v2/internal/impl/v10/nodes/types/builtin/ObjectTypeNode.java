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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.impl.commons.nodes.PropertyNode;
import org.raml.v2.internal.framework.nodes.AbstractRamlNode;
import org.raml.v2.internal.framework.nodes.IntegerNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.impl.v10.nodes.types.InheritedPropertiesInjectedNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.utils.NodeUtils;

import static org.raml.v2.internal.utils.NodeUtils.getType;

public class ObjectTypeNode extends AbstractRamlNode implements ObjectNode, TypeNode
{

    private List<InheritedPropertiesInjectedNode> inheritedProperties = Lists.newArrayList();

    private boolean resolvedType = false;

    public ObjectTypeNode()
    {
    }

    protected ObjectTypeNode(ObjectTypeNode node)
    {
        super(node);
    }

    public List<PropertyNode> getProperties()
    {
        ArrayList<PropertyNode> result = new ArrayList<>();
        List<Node> properties = Lists.newArrayList();
        if (getSource().get("properties") != null)
        {
            properties = getSource().get("properties").getChildren();
        }
        else if (NodeUtils.getType(this) instanceof StringNode)
        {
            String typeName = ((StringNode) NodeUtils.getType(this)).getValue();
            Node type = NodeUtils.getType(typeName, this);
            if (type != null && type instanceof ObjectTypeNode)
            {
                return ((ObjectTypeNode) type).getProperties();
            }

        }
        for (Node property : properties)
        {
            result.add((PropertyNode) property);
        }
        return result;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ObjectTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    public void addInheritedProperties(InheritedPropertiesInjectedNode node)
    {
        this.inheritedProperties.add(node);
    }

    public void setInheritedProperties(List<InheritedPropertiesInjectedNode> inheritedProperties)
    {
        this.inheritedProperties = inheritedProperties;
    }

    public List<InheritedPropertiesInjectedNode> getInheritedProperties()
    {
        return this.inheritedProperties;
    }

    @Override
    public <T> T visit(TypeNodeVisitor<T> visitor)
    {
        return visitor.visitObject(this);
    }

    public boolean isArray()
    {
        return NodeUtils.getType(this) instanceof StringNode && "array".equals(((StringNode) NodeUtils.getType(this)).getValue());
    }

    public Integer getMinProperties()
    {
        return getIntFacet("minProperties");
    }

    public Integer getMaxProperties()
    {
        return getIntFacet("maxProperties");
    }

    private Integer getIntFacet(String facetName)
    {
        if (this.get(facetName) != null && this.get(facetName) instanceof IntegerNode)
        {
            return ((IntegerNode) this.get(facetName)).getValue();
        }
        return null;
    }

    public void markAsResolved()
    {
        this.resolvedType = true;
    }

    public boolean isResolved()
    {
        return this.resolvedType;
    }

    public boolean isAllowAdditionalProperties()
    {
        Node additionalProperties = this.get("additionalProperties");
        if (additionalProperties == null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
