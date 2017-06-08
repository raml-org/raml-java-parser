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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.type.UnionResolvedType;
import org.raml.yagi.framework.nodes.AbstractObjectNode;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

/**
 * Represents a node that declares an explicit type.
 * It's itself a TypeExpressionNode to allow inline inheritance of types.
 */
public class TypeDeclarationNode extends AbstractObjectNode implements TypeExpressionNode, OverlayableNode
{

    private ResolvedType resolvedType;
    private boolean resolvingType = false;

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
        else if (type != null)
        {
            result.add((TypeExpressionNode) type);
        }
        return result;
    }

    @Nullable
    public ResolvedType getResolvedType()
    {
        // Cache it to support recursive definitions
        if (resolvedType == null)
        {
            if (resolvingType)
            {
                this.replaceWith(RamlErrorNodeFactory.createRecurrentTypeDefinition(getTypeExpressionText()));
                return null;
            }
            else
            {
                resolvingType = true;
                resolvedType = resolveTypeDefinition();
                resolvingType = false;
            }
        }
        return resolvedType;
    }

    private ResolvedType resolveTypeDefinition()
    {
        ResolvedType result = resolveBaseType();

        // After result base definition we overwrite with local definitions
        if (result != null)
        {
            result = result.overwriteFacets(this);
        }
        return result;
    }

    private ResolvedType resolveBaseType()
    {
        final List<TypeExpressionNode> baseTypes = getBaseTypes();
        ResolvedType result = null;
        // First we inherit all base properties and merge with multiple inheritance
        for (TypeExpressionNode baseType : baseTypes)
        {
            final ResolvedType baseTypeDef = baseType.generateDefinition();
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
        if (result != null)
        {
            // The final resolved type should point to this node and not the the inherited one
            result = result.setTypeNode(this);
        }
        return result;
    }

    public void validateCanOverwrite()
    {
        ResolvedType result = resolveBaseType();
        // After result base definition we overwrite with local definitions
        if (result != null)
        {
            result.validateCanOverwriteWith(this);
        }
    }

    public void validateState()
    {
        final ResolvedType resolvedType = getResolvedType();
        if (resolvedType != null)
        {
            resolvedType.validateState();
        }
    }

    public List<CustomFacetDefinitionNode> getCustomFacets()
    {
        return findDescendantsWith(CustomFacetDefinitionNode.class);
    }

    private Node getTypeValue()
    {
        return org.raml.yagi.framework.util.NodeUtils.getType(this);
    }

    @Nullable
    public String getTypeName()
    {
        return getResolvedType() != null ? getResolvedType().getTypeName() : null;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new TypeDeclarationNode(this);
    }

    @Nullable
    @Override
    public ResolvedType generateDefinition()
    {
        return resolveTypeDefinition();
    }

    @Override
    public String getTypeExpressionText()
    {
        if (getParent() instanceof TypeDeclarationField)
        {
            return ((SimpleTypeNode) ((TypeDeclarationField) getParent()).getKey()).getLiteralValue();
        }
        else if (getSource() instanceof SimpleTypeNode)
        {
            return ((SimpleTypeNode) getSource()).getLiteralValue();
        }
        else
        {
            return getResolvedType().getBuiltinTypeName();
        }
    }

    public List<FacetNode> getFacets()
    {
        final List<FacetNode> result = new ArrayList<>();

        final List<Node> children = getChildren();
        for (Node child : children)
        {
            if (child instanceof FacetNode)
            {
                result.add((FacetNode) child);
            }
        }
        return result;
    }
}
