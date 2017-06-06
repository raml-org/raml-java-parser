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
package org.raml.v2.internal.impl.commons.type;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationField;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.type.UnionResolvedType;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

public abstract class BaseType implements ResolvedType
{

    protected ResolvedCustomFacets customFacets;
    private TypeExpressionNode typeExpressionNode;
    private String typeName;

    public BaseType(String typeName, TypeExpressionNode typeExpressionNode, ResolvedCustomFacets customFacets)
    {
        this.typeExpressionNode = typeExpressionNode;
        this.customFacets = customFacets;
        this.typeName = typeName;
    }

    public ResolvedType setTypeNode(TypeExpressionNode typeNode)
    {
        BaseType copy = copy();
        copy.typeExpressionNode = typeNode;
        return copy;
    }

    protected abstract BaseType copy();

    @Override
    public ResolvedCustomFacets customFacets()
    {
        return customFacets;
    }

    protected void overwriteFacets(BaseType from, TypeDeclarationNode node)
    {
        if (node.getParent() instanceof TypeDeclarationField)
        {
            from.typeName = ((SimpleTypeNode) ((TypeDeclarationField) node.getParent()).getKey()).getLiteralValue();
        }
        else if (!(node.getSource() instanceof SimpleTypeNode))
        {
            from.typeName = getBuiltinTypeName();
        }
    }

    @Override
    public final boolean accepts(ResolvedType valueType)
    {
        if (valueType instanceof UnionResolvedType)
        {
            List<ResolvedType> toAcceptOptions = ((UnionResolvedType) valueType).of();
            for (ResolvedType toAcceptOption : toAcceptOptions)
            {
                if (!doAccept(toAcceptOption))
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return doAccept(valueType);
        }
    }

    public boolean doAccept(ResolvedType resolvedType)
    {
        // Only accepts my types
        return this.getClass().equals(resolvedType.getClass());
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    @Nullable
    @Override
    public String getTypeName()
    {
        return typeName;
    }

    @Nullable
    @Override
    public String getBuiltinTypeName()
    {
        return null;
    }

    @Override
    public TypeExpressionNode getTypeExpressionNode()
    {
        return typeExpressionNode;
    }


    @Override
    public void validateState()
    {

    }

    @Nonnull
    public static String getTypeName(TypeExpressionNode typeExpressionNode, String defaultName)
    {
        if (typeExpressionNode.getParent() instanceof TypeDeclarationField)
        {
            return ((SimpleTypeNode) ((TypeDeclarationField) typeExpressionNode.getParent()).getKey()).getLiteralValue();
        }
        else
        {
            return defaultName;
        }
    }

}
