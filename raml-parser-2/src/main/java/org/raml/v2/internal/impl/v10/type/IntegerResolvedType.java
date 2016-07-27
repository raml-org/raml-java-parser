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
import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.yagi.framework.nodes.ErrorNode;

public class IntegerResolvedType extends NumberResolvedType
{

    public IntegerResolvedType(TypeDeclarationNode from)
    {
        super(from);
    }

    public IntegerResolvedType(TypeDeclarationNode declarationNode, XmlFacets xmlFacets, Number minimum, Number maximum, Number multiple, String format)
    {
        super(declarationNode, xmlFacets, minimum, maximum, multiple, format);
    }

    @Override
    public NumberResolvedType copy()
    {
        return new IntegerResolvedType(getTypeDeclarationNode(), getXmlFacets().copy(), getMinimum(), getMaximum(), getMultiple(), getFormat());
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitInteger(this);
    }

    @Override
    public ErrorNode validateFacets()
    {
        int min = getMinimum() != null ? getMinimum().intValue() : Integer.MIN_VALUE;
        int max = getMaximum() != null ? getMaximum().intValue() : Integer.MAX_VALUE;
        int mult = getMultiple() != null ? getMultiple().intValue() : 1;

        // Checking conflicts between the minimum and maximum facets
        if (max < min)
        {
            return RamlErrorNodeFactory.createInvalidFacet(
                    getTypeName(),
                    "maximum must be greater or equal than minimum");
        }

        // It must be at least one multiple of the number between the valid range
        if (getMultiple() != null && !hasValidMultiplesInRange(min, max, mult))
        {
            return RamlErrorNodeFactory.createInvalidFacet(
                    getTypeName(),
                    "It must be at least one multiple of " + mult + " in the given range");
        }


        // For each value in the list, it must be between minimum and maximum
        for (Number thisEnum : getEnums())
        {
            int value = (int) thisEnum;
            if (value < min || value > max)
            {
                return RamlErrorNodeFactory.createInvalidFacet(
                        getTypeName(),
                        "enums values must be between " + min + " and " + max);
            }

            if (value % mult != 0)
            {
                return RamlErrorNodeFactory.createInvalidFacet(
                        getTypeName(),
                        "enums values must have all values multiple of " + mult);
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
}
