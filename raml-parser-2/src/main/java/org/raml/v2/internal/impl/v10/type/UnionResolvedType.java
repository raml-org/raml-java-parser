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

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.List;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.v2.internal.impl.commons.type.BaseType;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.SchemaBasedResolvedType;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;

public class UnionResolvedType extends BaseType
{

    private List<ResolvedType> of;

    public UnionResolvedType(TypeExpressionNode typeNode, List<ResolvedType> of, ResolvedCustomFacets customFacets)
    {
        this(getTypeName(typeNode, defaultName(of)), typeNode, of, customFacets);
    }


    public UnionResolvedType(String name, TypeExpressionNode typeNode, List<ResolvedType> of, ResolvedCustomFacets customFacets)
    {
        super(name, typeNode, customFacets);
        this.of = of;
    }

    public List<ResolvedType> of()
    {
        return of;
    }

    static String defaultName(List<ResolvedType> of)
    {
        StringBuilder result = new StringBuilder();
        for (ResolvedType resolvedType : of)
        {
            if (result.length() == 0)
            {
                result = new StringBuilder(resolvedType.getTypeName());
            }
            else
            {
                result.append(" | ").append(resolvedType.getTypeName());
            }
        }
        return result.toString();
    }

    protected UnionResolvedType copy()
    {
        return new UnionResolvedType(getTypeName(), getTypeExpressionNode(), new ArrayList<>(of), customFacets.copy());
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final List<ResolvedType> result = new ArrayList<>();
        for (ResolvedType resolvedType : of())
        {
            ResolvedType overwriteFacets = resolvedType.overwriteFacets(from);
            if (overwriteFacets instanceof BaseType)
            {
                ((BaseType) overwriteFacets).setTypeName(resolvedType.getTypeName());
            }
            result.add(overwriteFacets);
        }
        return new UnionResolvedType(from, result, customFacets.overwriteFacets(from));
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
    public boolean doAccept(ResolvedType valueType)
    {
        for (ResolvedType resolvedType : of)
        {
            if (resolvedType.accepts(valueType))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitUnion(this);
    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        final Node parent = from.getParent();
        for (ResolvedType resolvedType : of)
        {
            if (parent.findDescendantsWith(ErrorNode.class).isEmpty())
            {
                resolvedType.validateCanOverwriteWith(from);
            }
        }
    }

    @Override
    public void validateState()
    {
        final Node parent = getTypeExpressionNode().getParent();
        for (ResolvedType resolvedType : of)
        {
            if (parent.findDescendantsWith(ErrorNode.class).isEmpty())
            {
                resolvedType.validateState();
            }
        }

        for (ResolvedType resolvedType : of)
        {
            if (resolvedType instanceof SchemaBasedResolvedType)
            {
                getTypeExpressionNode().replaceWith(RamlErrorNodeFactory.createInvalidFacetState(resolvedType.getTypeName(), "union type cannot be of an external type"));
            }
        }
    }

    protected ResolvedType mergeWith(List<ResolvedType> of)
    {
        final List<ResolvedType> combination = new ArrayList<>();
        ResolvedCustomFacets customFacets = this.customFacets.copy();
        for (ResolvedType localDefinition : of())
        {
            for (ResolvedType resolvedType : of)
            {
                customFacets.mergeWith(resolvedType.customFacets());
                combination.add(localDefinition.mergeFacets(resolvedType));
            }
        }

        return new UnionResolvedType(getTypeExpressionNode(), combination, customFacets);
    }
}
