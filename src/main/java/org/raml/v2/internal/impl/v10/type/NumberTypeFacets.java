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

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYArrayNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.utils.NodeSelector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class NumberTypeFacets extends BaseTypeFacets
{
    private Number minimum;
    private Number maximum;
    private Number multiple;
    private String format;
    private List<Number> enums = new ArrayList<>();

    public NumberTypeFacets()
    {
    }

    public NumberTypeFacets(Number minimum, Number maximum, Number multiple, String format)
    {
        this.minimum = minimum;
        this.maximum = maximum;
        this.multiple = multiple;
        this.format = format;
    }

    public NumberTypeFacets copy()
    {
        return new NumberTypeFacets(minimum, maximum, multiple, format);
    }

    @Override
    public TypeFacets overwriteFacets(TypeDeclarationNode from)
    {
        final NumberTypeFacets result = copy();
        result.setMinimum(NodeSelector.selectIntValue("minimum", from));
        result.setMaximum(NodeSelector.selectIntValue("maximum", from));
        result.setMultiple(NodeSelector.selectIntValue("multipleOf", from));
        result.setFormat(NodeSelector.selectStringValue("format", from));
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
    public TypeFacets mergeFacets(TypeFacets with)
    {
        final NumberTypeFacets result = copy();
        if (with instanceof NumberTypeFacets)
        {
            NumberTypeFacets numberTypeDefinition = (NumberTypeFacets) with;
            result.setMinimum(numberTypeDefinition.getMinimum());
            result.setMaximum(numberTypeDefinition.getMaximum());
            result.setMultiple(numberTypeDefinition.getMultiple());
            result.setFormat(numberTypeDefinition.getFormat());
            result.setEnums(numberTypeDefinition.getEnums());
        }
        return mergeFacets(result, with);
    }

    @Override
    public <T> T visit(TypeFacetsVisitor<T> visitor)
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
}
