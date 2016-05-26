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

import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class UnionTypeFacets implements TypeFacets
{
    private List<TypeFacets> of;

    public UnionTypeFacets(List<TypeFacets> of)
    {
        this.of = of;
    }

    public List<TypeFacets> of()
    {
        return of;
    }

    protected UnionTypeFacets copy()
    {
        return new UnionTypeFacets(new ArrayList<>(of));
    }

    @Override
    public TypeFacets overwriteFacets(TypeDeclarationNode from)
    {
        final List<TypeFacets> result = new ArrayList<>();
        final List<TypeFacets> of = of();
        for (TypeFacets typeFacets : of)
        {
            result.add(typeFacets.overwriteFacets(from));
        }
        return new UnionTypeFacets(result);
    }

    @Override
    public TypeFacets mergeFacets(TypeFacets with)
    {
        if (with instanceof UnionTypeFacets)
        {
            final List<TypeFacets> of = ((UnionTypeFacets) with).of();
            return mergeWith(of);
        }
        else
        {
            return mergeWith(singletonList(with));
        }
    }

    @Override
    public <T> T visit(TypeFacetsVisitor<T> visitor)
    {
        return visitor.visitUnion(this);
    }

    protected TypeFacets mergeWith(List<TypeFacets> of)
    {
        final List<TypeFacets> combination = new ArrayList<>();
        for (TypeFacets localDefinition : of())
        {
            for (TypeFacets typeFacets : of)
            {
                combination.add(localDefinition.mergeFacets(typeFacets));
            }
        }
        return new UnionTypeFacets(combination);
    }
}
