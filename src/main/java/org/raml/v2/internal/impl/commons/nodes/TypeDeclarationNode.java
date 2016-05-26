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

import org.raml.v2.internal.framework.nodes.AbstractRamlNode;
import org.raml.v2.internal.framework.nodes.ArrayNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.impl.v10.type.UnionTypeFacets;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TypeDeclarationNode extends AbstractRamlNode implements ObjectNode
{

    private TypeFacets typeFacets;

    public TypeDeclarationNode()
    {
    }

    protected TypeDeclarationNode(TypeDeclarationNode node)
    {
        super(node);
    }

    @Nonnull
    public List<TypeExpressionNode> getBaseTypes()
    {
        List<TypeExpressionNode> result = new ArrayList<>();
        final Node type = getTypeValue();
        if (type instanceof ArrayNode)
        {
            final List<Node> children = type.getChildren();
            for (Node child : children)
            {
                result.add((TypeExpressionNode) child);
            }
        }
        else
        {
            result.add((TypeExpressionNode) type);
        }
        return result;
    }

    public TypeFacets getTypeFacets()
    {
        // Cache it to support recursive definitions
        if (typeFacets == null)
        {
            typeFacets = resolveTypeDefinition();
        }
        return typeFacets;
    }

    protected TypeFacets resolveTypeDefinition()
    {
        final List<TypeExpressionNode> baseTypes = getBaseTypes();
        TypeFacets result = null;
        // First we inherit all base properties and merge with multiple inheritance
        for (TypeExpressionNode baseType : baseTypes)
        {
            final TypeFacets baseTypeDef = baseType.generateDefinition();
            if (result == null)
            {
                result = baseTypeDef;
            }
            else
            {
                // It can inherit from union and non union and in this case the result is a union so we flip the merge order
                if (baseTypeDef instanceof UnionTypeFacets && !(result instanceof UnionTypeFacets))
                {
                    result = baseTypeDef.mergeFacets(result);
                }
                else
                {
                    result = result.mergeFacets(baseTypeDef);
                }
            }
        }
        // After result base definition we overwrite with local definitions
        if (result != null)
        {
            result = result.overwriteFacets(this);
        }
        return result;
    }

    private Node getTypeValue()
    {
        final Node type = get("type");
        return type == null ? get("schema") : type;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new TypeDeclarationNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }
}
