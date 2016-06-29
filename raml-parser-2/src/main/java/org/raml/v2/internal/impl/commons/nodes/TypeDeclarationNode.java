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

import org.raml.yagi.framework.nodes.AbstractObjectNode;
import org.raml.yagi.framework.nodes.AbstractRamlNode;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.type.UnionResolvedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TypeDeclarationNode extends AbstractObjectNode implements OverlayableNode
{

    private ResolvedType resolvedType;

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

    public ResolvedType getResolvedType()
    {
        // Cache it to support recursive definitions
        if (resolvedType == null)
        {
            resolvedType = resolveTypeDefinition();
        }
        return resolvedType;
    }

    protected ResolvedType resolveTypeDefinition()
    {
        final List<TypeExpressionNode> baseTypes = getBaseTypes();
        ResolvedType result = null;
        // First we inherit all base properties and merge with multiple inheritance
        for (TypeExpressionNode baseType : baseTypes)
        {
            final ResolvedType baseTypeDef = baseType.generateDefinition(this);
            if (result == null)
            {
                result = baseTypeDef;
            }
            else
            {
                // It can inherit from union and non union and in this case the result is a union so we flip the merge order
                if (baseTypeDef instanceof UnionResolvedType && !(result instanceof UnionResolvedType))
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
        return org.raml.yagi.framework.util.NodeUtils.getType(this);
    }

    @Nullable
    public String getTypeName()
    {
        if (getParent() instanceof TypeDeclarationField)
        {
            return ((SimpleTypeNode) ((TypeDeclarationField) getParent()).getKey()).getLiteralValue();
        }
        else
        {
            return null;
        }
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new TypeDeclarationNode(this);
    }

}
