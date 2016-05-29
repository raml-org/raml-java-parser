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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class UnionResolvedType implements ResolvedType
{
    private List<ResolvedType> of;
    private TypeDeclarationNode typeNode;

    public UnionResolvedType(List<ResolvedType> of)
    {
        this.of = of;
    }

    public List<ResolvedType> of()
    {
        return of;
    }

    protected UnionResolvedType copy()
    {
        return new UnionResolvedType(new ArrayList<>(of));
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        typeNode = from;
        final List<ResolvedType> result = new ArrayList<>();
        final List<ResolvedType> of = of();
        for (ResolvedType resolvedType : of)
        {
            result.add(resolvedType.overwriteFacets(from));
        }
        return new UnionResolvedType(result);
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
        return new UnionResolvedType(combination);
    }
}
