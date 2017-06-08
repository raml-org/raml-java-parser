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

import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.FORMAT_KEY_NAME;
import static org.raml.yagi.framework.util.NodeSelector.selectStringValue;

import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;

public class DateTimeResolvedType extends XmlFacetsCapableType
{

    private String format;

    public DateTimeResolvedType(String typeName, TypeExpressionNode declarationNode, XmlFacets xmlFacets, String format, ResolvedCustomFacets customFacets)
    {
        super(typeName, declarationNode, xmlFacets, customFacets);
        this.format = format;
    }

    public DateTimeResolvedType(TypeExpressionNode from)
    {
        super(getTypeName(from, TypeId.DATE_TIME.getType()), from, new ResolvedCustomFacets(FORMAT_KEY_NAME));
    }

    protected DateTimeResolvedType copy()
    {
        return new DateTimeResolvedType(getTypeName(), getTypeExpressionNode(), getXmlFacets().copy(), format, customFacets.copy());
    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        customFacets.validate(from);
        final Raml10Grammar raml10Grammar = new Raml10Grammar();
        final AnyOfRule facetRule = new AnyOfRule()
                                                   .add(raml10Grammar.formatField())
                                                   .addAll(customFacets.getRules());
        TypesUtils.validateAllWith(facetRule, from.getFacets());
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final DateTimeResolvedType result = copy();
        result.setFormat(selectStringValue(FORMAT_KEY_NAME, from));
        result.customFacets = customFacets.overwriteFacets(from);
        return overwriteFacets(result, from);
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final DateTimeResolvedType result = copy();
        if (with instanceof DateTimeResolvedType)
        {
            result.setFormat(((DateTimeResolvedType) with).getFormat());
        }
        result.customFacets = result.customFacets.mergeWith(with.customFacets());
        return mergeFacets(result, with);
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitDateTime(this);
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        if (format != null)
        {
            this.format = format;
        }
    }

    @Nullable
    @Override
    public String getBuiltinTypeName()
    {
        return TypeId.DATE_TIME.getType();
    }

}
