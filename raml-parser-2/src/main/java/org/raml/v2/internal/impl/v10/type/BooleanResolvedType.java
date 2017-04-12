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

import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYArrayNode;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BooleanResolvedType extends XmlFacetsCapableType
{
    private List<String> enums = new ArrayList<>();

    public BooleanResolvedType(TypeExpressionNode declarationNode, XmlFacets xmlFacets, ResolvedCustomFacets customFacets)
    {
        super(declarationNode, xmlFacets, customFacets);
    }

    public BooleanResolvedType(TypeExpressionNode from)
    {
        super(from, new ResolvedCustomFacets());
    }

    protected BooleanResolvedType copy()
    {
        return new BooleanResolvedType(getTypeDeclarationNode(), getXmlFacets().copy(), customFacets.copy());
    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        customFacets.validate(from);
        final Raml10Grammar raml10Grammar = new Raml10Grammar();
        final AnyOfRule facetRule = new AnyOfRule()
                                                   .add(raml10Grammar.enumField())
                                                   .addAll(customFacets.getRules());
        TypesUtils.validateAllWith(facetRule, from.getFacets());
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final BooleanResolvedType copy = copy();
        copy.customFacets = copy.customFacets().overwriteFacets(from);
        copy.setEnums(getEnumValues(from));
        return overwriteFacets(copy, from);
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final BooleanResolvedType copy = copy();
        copy.customFacets = copy.customFacets().mergeWith(with.customFacets());
        return mergeFacets(copy, with);
    }

    @Nonnull
    private List<String> getEnumValues(Node typeNode)
    {
        Node values = typeNode.get("enum");
        List<String> enumValues = new ArrayList<>();
        if (values != null && values instanceof SYArrayNode)
        {
            for (Node node : values.getChildren())
            {
                enumValues.add(((SimpleTypeNode) node).getLiteralValue());
            }
        }
        return enumValues;
    }

    public List<String> getEnums()
    {
        return enums;
    }

    public void setEnums(List<String> enums)
    {
        if (enums != null && !enums.isEmpty())
        {
            this.enums = enums;
        }
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitBoolean(this);
    }

    @Nullable
    @Override
    public String getBuiltinTypeName()
    {
        return TypeId.BOOLEAN.getType();
    }

}
