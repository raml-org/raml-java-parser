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
package org.raml.v2.internal.impl.v10.type;

import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.v10.nodes.UnionTypeExpressionNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNodeImpl;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class UnionResolvedType implements ResolvedType
{
    private List<ResolvedType> of;
    private TypeDeclarationNode typeNode;

    public UnionResolvedType(TypeDeclarationNode typeNode, List<ResolvedType> of)
    {
        this.typeNode = findUnionExpressionType(typeNode, typeNode.getChildren());
        this.of = of;
    }

    public List<ResolvedType> of()
    {
        return of;
    }

    protected UnionResolvedType copy()
    {
        return new UnionResolvedType(typeNode, new ArrayList<>(of));
    }

    protected TypeDeclarationNode findUnionExpressionType(TypeDeclarationNode typeNode, List<Node> children)
    {
        UnionTypeExpressionNode foundUnion = null;
        for (Node exp : children)
        {
            if (exp instanceof UnionTypeExpressionNode)
                foundUnion = (UnionTypeExpressionNode) exp;
        }
        if (foundUnion != null)
        {
            TypeDeclarationNode cloned = (TypeDeclarationNode) typeNode.copy();
            cloned.setParent(typeNode.getParent());
            cloned.removeChildren();
            KeyValueNodeImpl expressionNode = new KeyValueNodeImpl(new StringNodeImpl("type"), foundUnion);
            cloned.addChild(expressionNode);
            return cloned;
        }
        else
        {
            for (Node child : children)
            {
                TypeDeclarationNode tmp = findUnionExpressionType(typeNode, child.getChildren());
                if (tmp != null)
                    return tmp;
            }
            return null;
        }
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final List<ResolvedType> result = new ArrayList<>();
        final List<ResolvedType> of = of();
        for (ResolvedType resolvedType : of)
        {
            result.add(resolvedType.overwriteFacets(from));
        }
        return new UnionResolvedType(from, result);
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        if (with instanceof UnionResolvedType)
        {
            final List<ResolvedType> of = ((UnionResolvedType) with).of();
            return mergeWith(of);
        }
        else
        {
            return mergeWith(singletonList(with));
        }
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitUnion(this);
    }

    @Nullable
    @Override
    public String getTypeName()
    {
        return typeNode != null ? typeNode.getTypeName() : null;
    }

    @Override
    public TypeDeclarationNode getTypeDeclarationNode()
    {
        return typeNode;
    }

    protected ResolvedType mergeWith(List<ResolvedType> of)
    {
        final List<ResolvedType> combination = new ArrayList<>();
        for (ResolvedType localDefinition : of())
        {
            for (ResolvedType resolvedType : of)
            {
                combination.add(localDefinition.mergeFacets(resolvedType));
            }
        }
        return new UnionResolvedType(typeNode, combination);
    }
}
