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

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ROUND_CEILING;
import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.ZERO;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.FORMAT_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MAXIMUM_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MINIMUM_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MULTIPLE_OF_KEY_NAME;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYArrayNode;
import org.raml.yagi.framework.util.NodeSelector;

public class NumberResolvedType extends XmlFacetsCapableType
{
    private Number minimum;
    private Number maximum;
    private Number multiple;
    private String format;
    private List<Number> enums = new ArrayList<>();

    public NumberResolvedType(TypeExpressionNode from)
    {
        super(from, new ResolvedCustomFacets(MINIMUM_KEY_NAME, MAXIMUM_KEY_NAME, MULTIPLE_OF_KEY_NAME, FORMAT_KEY_NAME));
    }

    public NumberResolvedType(TypeExpressionNode declarationNode, XmlFacets xmlFacets, Number minimum, Number maximum, Number multiple, String format, ResolvedCustomFacets customFacets)
    {
        super(declarationNode, xmlFacets, customFacets);
        this.minimum = minimum;
        this.maximum = maximum;
        this.multiple = multiple;
        this.format = format;
    }

    public NumberResolvedType copy()
    {
        return new NumberResolvedType(getTypeDeclarationNode(), getXmlFacets().copy(), minimum, maximum, multiple, format, customFacets.copy());
    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        customFacets.validate(from);
        final Raml10Grammar raml10Grammar = new Raml10Grammar();
        final AnyOfRule facetRule = new AnyOfRule()
                                                   .add(raml10Grammar.minimumField(raml10Grammar.numberType()))
                                                   .add(raml10Grammar.maximumField(raml10Grammar.numberType()))
                                                   .add(raml10Grammar.numberFormat())
                                                   .add(raml10Grammar.enumField())
                                                   .add(raml10Grammar.multipleOfField(raml10Grammar.numberType()))
                                                   .addAll(customFacets.getRules());
        TypesUtils.validateAllWith(facetRule, from.getFacets());
    }

    @Override
    public boolean doAccept(ResolvedType valueType)
    {
        return valueType instanceof NumberResolvedType;
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final NumberResolvedType result = copy();
        result.customFacets = customFacets.overwriteFacets(from);
        result.setMinimum(NodeSelector.selectNumberValue(MINIMUM_KEY_NAME, from));
        result.setMaximum(NodeSelector.selectNumberValue(MAXIMUM_KEY_NAME, from));
        result.setMultiple(NodeSelector.selectNumberValue(MULTIPLE_OF_KEY_NAME, from));
        result.setFormat(NodeSelector.selectStringValue(FORMAT_KEY_NAME, from));
        result.setEnums(getEnumValues(from));
        return overwriteFacets(result, from);
    }

    @Nonnull
    private List<Number> getEnumValues(Node typeNode)
    {

        Node values = typeNode.get("enum");
        List<Number> enumValues = new ArrayList<>();
        if (values != null && values instanceof SYArrayNode)
        {
            for (Node node : values.getChildren())
            {
                enumValues.add((Number) ((SimpleTypeNode) node).getValue());
            }
        }
        return enumValues;
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final NumberResolvedType result = copy();
        if (with instanceof NumberResolvedType)
        {
            NumberResolvedType numberTypeDefinition = (NumberResolvedType) with;
            result.setMinimum(numberTypeDefinition.getMinimum());
            result.setMaximum(numberTypeDefinition.getMaximum());
            result.setMultiple(numberTypeDefinition.getMultiple());
            result.setFormat(numberTypeDefinition.getFormat());
            result.setEnums(numberTypeDefinition.getEnums());
        }
        result.customFacets = result.customFacets.mergeWith(with.customFacets());
        return mergeFacets(result, with);
    }

    @Override
    public void validateState()
    {
        super.validateState();
        final ErrorNode errorNode = validateFacets();
        if (errorNode != null)
        {
            getTypeDeclarationNode().replaceWith(errorNode);
        }
    }

    public ErrorNode validateFacets()
    {
        BigDecimal min = minimum != null ? new BigDecimal(minimum.toString()) : new BigDecimal(Double.MIN_VALUE);
        BigDecimal max = maximum != null ? new BigDecimal(maximum.toString()) : new BigDecimal(Double.MAX_VALUE);
        BigDecimal mult = multiple != null ? new BigDecimal(multiple.toString()) : null;

        // Checking conflicts between the minimum and maximum facets if both are set
        if (max.compareTo(min) < 0)
        {
            return RamlErrorNodeFactory.createInvalidFacetState(
                    getTypeName(),
                    "maximum must be greater than or equal to minimum");
        }

        // It must be at least one multiple of the number between the valid range
        if (mult != null && !hasValidMultiplesInRange(min, max, mult))
        {
            return RamlErrorNodeFactory.createInvalidFacetState(
                    getTypeName(),
                    "There must be at least one multiple of " + mult + " in the given range");
        }

        // For each value in the list, it must be between minimum and maximum
        for (Number thisEnum : enums)
        {
            BigDecimal value = new BigDecimal(thisEnum.toString());

            if (value.compareTo(min) < 0 || value.compareTo(max) > 0)
            {
                return RamlErrorNodeFactory.createInvalidFacetState(
                        getTypeName(),
                        "enum values must be between " + minimum + " and " + maximum);
            }

            if (mult != null && value.remainder(mult).compareTo(BigDecimal.ZERO) != 0)
            {
                return RamlErrorNodeFactory.createInvalidFacetState(
                        getTypeName(),
                        "enum values must be multiple of " + mult);
            }
        }

        return null;
    }

    private boolean hasValidMultiplesInRange(BigDecimal min, BigDecimal max, BigDecimal mult)
    {
        // Zero is multiple of every number
        if (mult.compareTo(BigDecimal.ZERO) == 0)
        {
            return true;
        }

        BigDecimal divideMax = max.divide(mult, 0, ROUND_DOWN);
        BigDecimal divideMin = min.divide(mult, 0, ROUND_CEILING);
        BigDecimal subtract = divideMax.subtract(divideMin);
        BigDecimal plusOne = subtract.add(ONE);
        BigDecimal max0 = plusOne.max(ZERO);
        BigDecimal numberOfMultiplesInRange = max0.setScale(0, ROUND_DOWN);

        return numberOfMultiplesInRange.compareTo(ZERO) > 0;
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitNumber(this);
    }

    public Number getMinimum()
    {
        return minimum;
    }


    public List<Number> getEnums()
    {
        return enums;
    }

    public void setEnums(List<Number> enums)
    {
        if (enums != null && !enums.isEmpty())
        {
            this.enums = enums;
        }
    }

    private void setMinimum(Number minimum)
    {
        if (minimum != null)
        {
            this.minimum = minimum;
        }
    }

    public Number getMaximum()
    {
        return maximum;
    }

    private void setMaximum(Number maximum)
    {
        if (maximum != null)
        {
            this.maximum = maximum;
        }
    }

    public Number getMultiple()
    {
        return multiple;
    }

    private void setMultiple(Number multiple)
    {
        if (multiple != null)
        {
            this.multiple = multiple;
        }
    }

    public String getFormat()
    {
        return format;
    }

    private void setFormat(String format)
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
        return TypeId.NUMBER.getType();
    }

}
