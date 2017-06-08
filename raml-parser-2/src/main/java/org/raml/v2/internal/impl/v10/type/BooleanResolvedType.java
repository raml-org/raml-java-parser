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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.nodes.BooleanNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.snakeyaml.SYArrayNode;

import java.util.ArrayList;
import java.util.List;

public class BooleanResolvedType extends XmlFacetsCapableType
{

    private List<Boolean> enums = new ArrayList<>();

    public BooleanResolvedType(String typeName, TypeExpressionNode declarationNode, XmlFacets xmlFacets, ResolvedCustomFacets customFacets)
    {
        super(typeName, declarationNode, xmlFacets, customFacets);
    }

    public BooleanResolvedType(TypeExpressionNode from)
    {
        super(getTypeName(from, TypeId.BOOLEAN.getType()), from, new ResolvedCustomFacets());
    }

    protected BooleanResolvedType copy()
    {
        return new BooleanResolvedType(getTypeName(), getTypeExpressionNode(), getXmlFacets().copy(), customFacets.copy());
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

    @Nonnull
    private List<Boolean> getEnumValues(Node typeNode)
    {

        Node values = typeNode.get("enum");
        List<Boolean> enumValues = new ArrayList<>();
        if (values != null && values instanceof SYArrayNode)
        {
            for (Node node : values.getChildren())
            {
                if (node instanceof BooleanNode)
                {
                    enumValues.add(((BooleanNode) node).getValue());
                }
            }
        }
        return enumValues;
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final BooleanResolvedType copy = copy();
        copy.customFacets = copy.customFacets().mergeWith(with.customFacets());
        if (with instanceof BooleanResolvedType)
        {
            copy.setEnums(((BooleanResolvedType) with).getEnums());
        }
        return mergeFacets(copy, with);
    }

    public void setEnums(List<Boolean> enums)
    {
        if (enums != null)
        {
            this.enums = enums;
        }
    }

    public List<Boolean> getEnums()
    {
        return enums;
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
