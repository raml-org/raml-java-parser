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

import org.raml.v2.api.model.v08.system.types.AnyType;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import javax.annotation.Nullable;

public abstract class BaseType implements ResolvedType
{
    protected ResolvedCustomFacets customFacets;
    private TypeDeclarationNode typeNode;

    public BaseType(TypeDeclarationNode typeNode, ResolvedCustomFacets customFacets)
    {
        this.typeNode = typeNode;
        this.customFacets = customFacets;
    }

    protected void setTypeNode(TypeDeclarationNode typeNode)
    {
        this.typeNode = typeNode;
    }

    @Override
    public ResolvedCustomFacets customFacets()
    {
        return customFacets;
    }

    @Override
    public boolean inheritsFrom(ResolvedType valueType)
    {
        return valueType instanceof AnyType || getClass().equals(valueType.getClass());
    }

    @Nullable
    @Override
    public String getTypeName()
    {
        return getTypeDeclarationNode() != null ? getTypeDeclarationNode().getTypeName() : null;
    }

    @Override
    public TypeDeclarationNode getTypeDeclarationNode()
    {
        return typeNode;
    }


    @Override
    public void validateState()
    {

    }
}
