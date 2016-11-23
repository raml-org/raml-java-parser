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

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;

public class AnyResolvedType extends XmlFacetsCapableType
{


    public AnyResolvedType(TypeExpressionNode declarationNode, XmlFacets xmlFacets, ResolvedCustomFacets customFacets)
    {
        super(declarationNode, xmlFacets, customFacets);
    }

    public AnyResolvedType(TypeExpressionNode declarationNode)
    {
        super(declarationNode, new ResolvedCustomFacets());
    }

    protected AnyResolvedType copy()
    {
        return new AnyResolvedType(getTypeDeclarationNode(), getXmlFacets().copy(), customFacets.copy());
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final AnyResolvedType copy = copy();
        copy.customFacets = copy.customFacets.overwriteFacets(from);
        return copy;
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final AnyResolvedType copy = copy();
        copy.customFacets = copy.customFacets.mergeWith(with.customFacets());
        return copy;
    }

    @Override
    public boolean doAccept(ResolvedType valueType)
    {
        return true;
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitAny(this);
    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        customFacets.validate(from);
        final AnyOfRule facetRule = new AnyOfRule().addAll(customFacets.getRules());
        TypesUtils.validateAllWith(facetRule, from.getFacets());
    }

}
