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
import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.nodes.ErrorNode;

public class IntegerResolvedType extends NumberResolvedType
{

    public IntegerResolvedType(TypeExpressionNode from)
    {
        super(from);
    }

    public IntegerResolvedType(String typeName, TypeExpressionNode declarationNode, XmlFacets xmlFacets, Number minimum, Number maximum, Number multiple, String format, ResolvedCustomFacets copy)
    {
        super(typeName, declarationNode, xmlFacets, minimum, maximum, multiple, format, copy);
    }

    @Override
    public NumberResolvedType copy()
    {
        return new IntegerResolvedType(getTypeName(), getTypeExpressionNode(), getXmlFacets().copy(), getMinimum(), getMaximum(), getMultiple(), getFormat(), customFacets.copy());
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitInteger(this);
    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        customFacets.validate(from);
        final Raml10Grammar raml10Grammar = new Raml10Grammar();
        final AnyOfRule facetRule = new AnyOfRule()
                                                   .add(raml10Grammar.minimumField(raml10Grammar.integerType()))
                                                   .add(raml10Grammar.maximumField(raml10Grammar.integerType()))
                                                   .add(raml10Grammar.numberFormat())
                                                   .add(raml10Grammar.enumField())
                                                   .add(raml10Grammar.multipleOfField(raml10Grammar.positiveIntegerType(false, Long.MAX_VALUE)))
                                                   .addAll(customFacets.getRules());
        TypesUtils.validateAllWith(facetRule, from.getFacets());
    }

    @Override
    public ErrorNode validateFacets()
    {
        long min = getMinimum() != null ? getMinimum().longValue() : Long.MIN_VALUE;
        long max = getMaximum() != null ? getMaximum().longValue() : Long.MAX_VALUE;
        long mult = getMultiple() != null ? getMultiple().longValue() : 1;

        // Checking conflicts between the minimum and maximum facets
        if (max < min)
        {
            return RamlErrorNodeFactory.createInvalidFacetState(
                    getTypeName(), "maximum must be greater than or equal to minimum");
        }

        // It must be at least one multiple of the number between the valid range
        if (getMultiple() != null && !hasValidMultiplesInRange(min, max, mult))
        {
            return RamlErrorNodeFactory.createInvalidFacetState(
                    getTypeName(),
                    "There must be at least one multiple of " + mult + " in the given range");
        }


        // For each value in the list, it must be between minimum and maximum
        for (Number thisEnum : getEnums())
        {
            long value = thisEnum.longValue();
            if (value < min || value > max)
            {
                return RamlErrorNodeFactory.createInvalidFacetState(
                        getTypeName(),
                        "enum values must be between " + min + " and " + max);
            }

            if (value % mult != 0)
            {
                return RamlErrorNodeFactory.createInvalidFacetState(
                        getTypeName(),
                        "enum values must be multiple of " + mult);
            }
        }

        return null;
    }

    private boolean hasValidMultiplesInRange(double min, double max, double mult)
    {
        // Zero is multiple of every number
        if (mult == 0)
        {
            return true;
        }

        double numberOfMultiplesInRange = Math.max(Math.floor(max / mult) - Math.ceil(min / mult) + 1, 0);
        return numberOfMultiplesInRange > 0;
    }

    @Nullable
    @Override
    public String getBuiltinTypeName()
    {
        return TypeId.INTEGER.getType();
    }

}
