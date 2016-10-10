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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.NamedTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.type.UnionResolvedType;
import org.raml.yagi.framework.nodes.ErrorNode;

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

    @Override
    public void validateHierarchy() {

        Map<TypeExpressionNode, Set<String>> parentTypes = new HashMap<>();

        TypeDeclarationNode typeDeclarationNode = this.getTypeDeclarationNode();
        if ( typeDeclarationNode.getBaseTypes().size() > 1 ) {

            for (TypeExpressionNode typeExpressionNode : typeDeclarationNode.getBaseTypes()) {

                Set<String> nativeBaseTypes = getNativeBaseTypes(typeExpressionNode);
                parentTypes.put(typeExpressionNode, nativeBaseTypes);
            }

            Set<String> types = new HashSet<>();
            for (Set<String> typesPerParentType : parentTypes.values()) {
                types.addAll(typesPerParentType);
            }

            if ( types.size() <= 1) {
                return;
            }

            getTypeDeclarationNode().replaceWith(new ErrorNode(typeDeclarationNode.getTypeName() + " contains incompatible parent types: " + buildErrorFromMap(parentTypes)));
        }
    }

    private String buildErrorFromMap(Map<TypeExpressionNode, Set<String>> parentTypes) {

        StringBuilder buffer = new StringBuilder();
        for (TypeExpressionNode typeExpressionNode : parentTypes.keySet()) {

            buffer.append(typeExpressionNode.getTypeExpressionText());
            if (typeExpressionNode instanceof NativeTypeExpressionNode) {
                buffer.append(" is a base type ");
                continue;
            } else {
                buffer.append(" is extending ");
            }
            buffer.append(parentTypes.get(typeExpressionNode));
            buffer.append(" ");
        }

        return buffer.toString();
    }

    private Set<String> getNativeBaseTypes(TypeExpressionNode typeExpressionNode) {

        Set<String> nativeBaseTypes = new HashSet<>();

        if ( typeExpressionNode instanceof NamedTypeExpressionNode ) {

            NamedTypeExpressionNode namedTypeExpressionNode = (NamedTypeExpressionNode) typeExpressionNode;
            TypeDeclarationNode tdn = (TypeDeclarationNode) namedTypeExpressionNode.getRefNode();
            if ( tdn == null ) {
                return nativeBaseTypes;
            }

            for (TypeExpressionNode expressionNode : tdn.getBaseTypes()) {

                nativeBaseTypes.addAll(getNativeBaseTypes(expressionNode));
            }

        } else {

            if ( typeExpressionNode instanceof  NativeTypeExpressionNode ) {

                nativeBaseTypes.add(typeExpressionNode.getTypeExpressionText());
            }
        }

        return nativeBaseTypes;
    }
}
