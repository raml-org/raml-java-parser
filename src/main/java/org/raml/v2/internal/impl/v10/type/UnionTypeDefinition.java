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

import org.raml.v2.internal.impl.commons.type.TypeDefinition;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class UnionTypeDefinition implements TypeDefinition
{
    private List<TypeDefinition> of;

    public UnionTypeDefinition(List<TypeDefinition> of)
    {
        this.of = of;
    }

    public List<TypeDefinition> of()
    {
        return of;
    }

    protected UnionTypeDefinition copy()
    {
        return new UnionTypeDefinition(new ArrayList<>(of));
    }

    @Override
    public TypeDefinition overwriteFacets(TypeDeclarationNode from)
    {
        final List<TypeDefinition> result = new ArrayList<>();
        final List<TypeDefinition> of = of();
        for (TypeDefinition typeDefinition : of)
        {
            result.add(typeDefinition.overwriteFacets(from));
        }
        return new UnionTypeDefinition(result);
    }

    @Override
    public TypeDefinition mergeFacets(TypeDefinition with)
    {
        if (with instanceof UnionTypeDefinition)
        {
            final List<TypeDefinition> of = ((UnionTypeDefinition) with).of();
            return mergeWith(of);
        }
        else
        {
            return mergeWith(singletonList(with));
        }
    }

    @Override
    public <T> T visit(TypeDefinitionVisitor<T> visitor)
    {
        return visitor.visitUnion(this);
    }

    protected TypeDefinition mergeWith(List<TypeDefinition> of)
    {
        final List<TypeDefinition> combination = new ArrayList<>();
        for (TypeDefinition localDefinition : of())
        {
            for (TypeDefinition typeDefinition : of)
            {
                combination.add(localDefinition.mergeFacets(typeDefinition));
            }
        }
        return new UnionTypeDefinition(combination);
    }
}
